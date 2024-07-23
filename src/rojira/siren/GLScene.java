package rojira.siren;

import static rojira.jsi4.LibConsole.cwarn;
import static rojira.jsi4.LibText.fmt;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import rojira.jsi4.util.gui.InputListener;

/**
 * Adapter class for GLRenderer and InputListener
 */
public abstract class GLScene implements GLRenderer, InputListener
{
	Siren siren;

	boolean enable_update = true;
	
	boolean enable_display = true;
	
	boolean enable_ui = true;
	
	void set_engine( Siren engine )
	{
		if( this.siren != null ) cwarn.println( fmt( "This object (%s) has already been added to a Siren engine (%s)", this, this.siren ) );
		
		this.siren = engine;
	}

	public Siren get_engine()
	{
		return siren;
	}
	
	@Override
	public void initGL() throws Exception
	{
	}
	
	@Override
	public void updateGL( double dt )
	{
	}
	
	@Override
	public void displayGL()
	{
	}
	
	@Override
	public void key_typed( char key, int code, KeyEvent e )
	{
	}
	
	@Override
	public void key_pressed( char key, int code, KeyEvent e )
	{
	}
	
	@Override
	public void key_released( char key, int code, KeyEvent e )
	{
	}
	
	@Override
	public void mouse_clicked( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_pressed( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_released( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_entered( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_exited( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_dragged( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_moved( MouseEvent e )
	{
	}
	
	@Override
	public void mouse_wheel_moved( MouseWheelEvent e )
	{
	}
	
	public boolean get_enable_update()
	{
		return this.enable_update;
	}

	public void enable_update( boolean enable_update )
	{
		this.enable_update = enable_update;
	}

	public boolean get_enable_display()
	{
		return this.enable_display;
	}

	public void enable_display( boolean enable_display )
	{
		this.enable_display = enable_display;
	}

	public boolean get_enable_ui()
	{
		return this.enable_ui;
	}

	public void enable_ui( boolean enable_ui )
	{
		this.enable_ui = enable_ui;
	}


}
