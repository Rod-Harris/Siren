package rojira.siren;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibConsole.cwarn;
import static rojira.jsi4.LibGUI.position_window;
import static rojira.jsi4.LibSystem.ex_to_string;
import static rojira.jsi4.LibSystem.systime;
import static rojira.sigl.GLUTWrap.glut;
import static rojira.sigl.GLUWrap.glu;
import static rojira.sigl.GLWrap.gl;
import static rojira.sigl.LibSiGL.check_error;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;

import rojira.jsi4.util.gui.InputAggregator;
import rojira.jsi4.util.gui.InputListener;
import rojira.jsi4.util.maths.Mat4d;


/**
 * <p>
 *
 * @author Rod Harris (Rojira Projects)
 */
public class Siren
{
	public final GLCapabilities gl_config = new GLCapabilities();

	private final GLFunc glfunc = new GLFunc( this );

	public ArrayList<GLScene> components = new ArrayList<GLScene>();

	public ArrayList<GLScene> new_components = new ArrayList<GLScene>();

	public JFrame glwin;

	public GLCanvas canvas;

	public final InputAggregator input_handler = new InputAggregator();

	public final TextRenderer text_renderer = new TextRenderer( new Font( "SansSerif", Font.BOLD, 36 ) );

	private boolean auto_enable;

	private boolean siren_exit;

	public final Mat4d camera_matrix = new Mat4d();


	public static void main( String[]  args ) throws Throwable
	{
	}


	public Siren()
	{
		cdebug.println( "new Siren engine: %s", this );

		//Thread.currentThread().dumpStack();

		gl_config.setRedBits( 8 );
		gl_config.setGreenBits( 8 );
		gl_config.setBlueBits( 8 );
		gl_config.setAlphaBits( 8 );

		gl_config.setDoubleBuffered( true );

		gl_config.setDepthBits( 24 );
		
		auto_enable = true;
	}


	public JFrame initialise_gl_window( int width, int height )
	{
		return initialise_window_for_gl( new JFrame(), width, height );
	}


	public JFrame initialise_window_for_gl( JFrame _window, int width, int height )
	{
		_window.setSize( width, height );

		return initialise_window_for_gl( _window );
	}


	public JFrame initialise_window_for_gl( JFrame _window )
	{
		if( glwin != null ) throw new IllegalStateException( "This Siren instance is already managing a gl window" );

		glwin = _window;

		canvas = new GLCanvas( gl_config );

		cdebug.println( "canvas: %s", canvas );

		canvas.addGLEventListener( glfunc );

		position_window( glwin, 0.5, 0.5 );

		glwin.add( canvas );

		input_handler.listen_to( canvas );
		
		input_handler.add_listener( new UIDistributor() );

		return glwin;
	}


	public void add_component( GLScene... components )
	{
		for( GLScene component : components )
		{
			this.new_components.add( component );
			
			if( auto_enable ) enable_component( component );
		}
	}


	public void enable_component( GLScene... components )
	{
		for( GLScene component : components )
		{
			component.enable_update = true;
				
			component.enable_display = true;
				
			component.enable_ui = true;
		}
	}


	public void disable_component( GLScene... components )
	{
		for( GLScene component : components )
		{
			component.enable_update = false;
				
			component.enable_display = false;
			
			component.enable_ui = false;
		}
	}




	public void exit()
	{
		siren_exit = true;
	}


	public void start_rendering()
	{
		start_rendering( -1 );
	}


	/**
	*	start rendering at the specified framerate
	*/
	public void start_rendering( int framerate )
	{
		glwin.setVisible( true );

		canvas.requestFocus();

		if( framerate > 50 )
		{
			cwarn.println( "requested framerate > 50fps, setting to unlimited" );

			framerate = -1;
		}

		Runnable display_caller = null;

		if( framerate > 0 )
		{
			double period = 1000.0f / framerate;

			final long millisecond_period = (long) period;

			display_caller = new Runnable()
			{
				public void run()
				{
					try
					{
						long t0, elapsed, wait_millis;

						while( ! siren_exit )
						{
							t0 = System.currentTimeMillis();

							display();

							elapsed = System.currentTimeMillis() - t0;

							wait_millis = millisecond_period - elapsed;

							if( wait_millis > 0 )
							{
								Thread.currentThread();
								Thread.sleep( wait_millis );
							}
						}
					}
					catch( Throwable ex )
					{
						cerr.println( ex_to_string( ex ) );

						return;
					}
				}
			};
		}
		else
		{
			assert framerate <= 0;

			cdebug.println( "unlimited framerate rendering" );

			display_caller = new Runnable()
			{
				public void run()
				{
					while( ! siren_exit )
					{
						display();
					}
				}
			};
		}

		assert display_caller != null;

		Thread display_thread = new Thread( display_caller );

/*
		display_thread.setDefaultUncaughtExceptionHandler
		(
			new Thread.UncaughtExceptionHandler()
			{
				public void uncaughtException( Thread t, Throwable e )
				{
					//cerr.println( ex_to_string( e ) );

					System.exit( 0 );
				}
			}
		);
*/

		display_thread.start();
	}


	public void exit_on_close()
	{
		glwin.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}


	public void exit_on_esc()
	{
		//exit_on_esc = true;
	}


	/*-------------------------------------------------------------------------------------------------*/
	
	
	private class UIDistributor implements InputListener
	{
		@Override
		public void key_typed( char key, int code, KeyEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.key_typed( key, code, e );
			}
		}
		
		@Override
		public void key_pressed( char key, int code, KeyEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.key_pressed( key, code, e );
			}
		}
		
		@Override
		public void key_released( char key, int code, KeyEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.key_released( key, code, e );
			}
		}
		
		@Override
		public void mouse_clicked( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_clicked( e );
			}
		}
		
		@Override
		public void mouse_pressed( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_pressed( e );
			}
		}
		
		@Override
		public void mouse_released( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_released( e );
			}
		}
		
		@Override
		public void mouse_entered( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_entered( e );
			}
		}
		
		@Override
		public void mouse_exited( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_exited( e );
			}
		}
		
		@Override
		public void mouse_dragged( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_dragged( e );
			}
		}
		
		@Override
		public void mouse_moved( MouseEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_moved( e );
			}
		}
		
		@Override
		public void mouse_wheel_moved( MouseWheelEvent e )
		{
			for( GLScene component : components )
			{
				if( ! component.enable_ui ) continue;
				
				component.mouse_wheel_moved( e );
			}
		}
	}
	
	
	/*-------------------------------------------------------------------------------------------------*/


	private void display()
	{
		canvas.display();
	}


	private class GLFunc implements GLEventListener
	{
		Siren siren;
		
		long last_frame;
		
		public GLFunc( Siren siren )
		{
			this.siren = siren;
		}

		public void init( GLAutoDrawable drawable )
		{
			gl = drawable.getGL();

			gl = new DebugGL( gl );

			glu = new GLU();

			glut = new GLUT();

			last_frame = systime();
		}


		public void display( GLAutoDrawable drawable )
		{
			if( new_components.size() > 0 )
			{
				for( GLScene component : new_components )
				{
					try
					{
						component.initGL();
						
						component.set_engine( siren );
						
						components.add( component );
					}
					catch( Exception ex )
					{
						if( ex instanceof RuntimeException ) throw (RuntimeException) ex;

						throw new RuntimeException( ex );
					}
				}

				new_components.clear();
			}

			long this_frame = systime();

			double dt = 0.001 * ( this_frame - last_frame );

			last_frame = this_frame;

			for( GLScene component : components )
			{
				if( component.enable_update )
				{
					component.updateGL( dt );
				}
			}

			for( GLScene component : components )
			{
				if( component.enable_display )
				{
					component.displayGL();
				}
			}

 			check_error();
		}

		public void displayChanged( GLAutoDrawable drawable, boolean mode_changed, boolean device_changed )
		{
			cwarn.println( "display changed:" );
			
			cwarn.println( "\tdrawable: " + drawable );
			
			cwarn.println( "\tmode_changed: " + mode_changed );
			
			cwarn.println( "\tdevice_changed: " + device_changed );
		}

		public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h )
		{
			cwarn.println( "reshape:" );
			
			cwarn.println( "\tdrawable: " + drawable );
			
			cwarn.println( "\tx: " + x );
			
			cwarn.println( "\ty: " + y );
			
			cwarn.println( "\tw: " + w );
			
			cwarn.println( "\th: " + h );
		}
	}
}
