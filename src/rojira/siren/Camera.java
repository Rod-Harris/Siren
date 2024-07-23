package rojira.siren;


import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibGUI.Blue;
import static rojira.jsi4.LibGUI.Green;
import static rojira.jsi4.LibGUI.Red;
import static rojira.jsi4.LibMaths.cross;
import static rojira.jsi4.LibMaths.deg;
import static rojira.jsi4.LibMaths.distance_between_point_and_plane;
import static rojira.jsi4.LibSystem.mtrace;
import static rojira.sigl.GLUWrap.gluPerspective;
import static rojira.sigl.GLWrap.GL_BACK;
import static rojira.sigl.GLWrap.GL_BACK_LEFT;
import static rojira.sigl.GLWrap.GL_BACK_RIGHT;
import static rojira.sigl.GLWrap.GL_LINES;
import static rojira.sigl.GLWrap.GL_PROJECTION;
import static rojira.sigl.GLWrap.glDrawBuffer;
import static rojira.sigl.GLWrap.glLoadIdentity;
import static rojira.sigl.GLWrap.glMatrixMode;
import static rojira.sigl.LibSiGL.begin_shape;
import static rojira.sigl.LibSiGL.camera;
import static rojira.sigl.LibSiGL.clip;
import static rojira.sigl.LibSiGL.colour;
import static rojira.sigl.LibSiGL.draw_vector;
import static rojira.sigl.LibSiGL.end_shape;
import static rojira.sigl.LibSiGL.get_modelview_matrix;
import static rojira.sigl.LibSiGL.pop_matrix;
import static rojira.sigl.LibSiGL.push_matrix;

import rojira.jsi4.util.maths.Mat4d;
import rojira.jsi4.util.maths.Plane;
import rojira.jsi4.util.maths.Vec3d;


public class Camera extends rojira.siren.GLScene
{
	public double xfov = 45;

	public double near = 0.01;

	public double far = 10000;
	
	public double half_eye_sep = 0.0;

	public final Vec3d position;

	public final Vec3d target;

	public final Vec3d up = new Vec3d( 0, 1, 0 );

	public final Vec3d X = new Vec3d();

	public final Vec3d Y = new Vec3d();

	public final Vec3d Z = new Vec3d();

	public final Vec3d nc = new Vec3d();

	public final Vec3d fc = new Vec3d();

	public boolean active = true;

	public boolean render = false;

	public Mat4d mv_tmp = new Mat4d();

	final Plane[] planes = new Plane[ 6 ];

	final Vec3d aux = new Vec3d();

	final Vec3d normal = new Vec3d();

	final Vec3d nearZ = new Vec3d();

	final Vec3d farZ = new Vec3d();


	int near_cp = 0;
	int far_cp = 1;
	int top_cp = 2;
	int bottom_cp = 3;
	int left_cp = 4;
	int right_cp = 5;


	public Camera()
	{
		this( new Vec3d(), new Vec3d() );
	}
	
	public Camera( Vec3d target )
	{
		this( new Vec3d(), target );
	}
	
	public Camera( Vec3d position, Vec3d target )
	{
		this.position = position;
		
		this.target = target;
	}

	@Override
	public void initGL()
	{
		cverbose.println( mtrace() );

		planes[ near_cp ] = new Plane();
		planes[ far_cp ] = new Plane();
		planes[ top_cp ] = new Plane();
		planes[ bottom_cp ] = new Plane();
		planes[ left_cp ] = new Plane();
		planes[ right_cp ] = new Plane();
	}


	@Override
	public void updateGL( double dt )
	{
		//cverbose.println( mtrace() );
	}


	@Override
	public void displayGL()
	{
		//cverbose.println( mtrace() );
		
		displayGL( RenderingMode.Mono, 0, 1 );
		/*
		

		double aspect = 0;

		double yfov = 0;
		
		push_matrix();
		


		if( active )
		{
			aspect = clip( 0, 0, get_engine().canvas.getWidth(), get_engine().canvas.getHeight() );

			yfov = xfov / aspect;

			glMatrixMode( GL_PROJECTION );

			glLoadIdentity();

			gluPerspective( yfov, aspect, near, far );

			//glDepthRange( near, far );

			//cverbose.println( "camera: %s, %s, %s", position, target, up );

			camera( position, target, up );

			get_modelview_matrix( mv_tmp );

			X.set( mv_tmp.m00, mv_tmp.m01, mv_tmp.m02 );
			Y.set( mv_tmp.m10, mv_tmp.m11, mv_tmp.m12 );
			Z.set( -mv_tmp.m20, -mv_tmp.m21, -mv_tmp.m22 );
			
			Vec3d eye = new Vec3d( position );
		
			Vec3d eye = new Vec3d( position );
		
			Vec3d eye = new Vec3d( position );
		}
		else
		{
			push_matrix();

			camera( position, target, up );

			get_modelview_matrix( mv_tmp );

			X.set( mv_tmp.m00, mv_tmp.m01, mv_tmp.m02 );
			Y.set( mv_tmp.m10, mv_tmp.m11, mv_tmp.m12 );
			Z.set( -mv_tmp.m20, -mv_tmp.m21, -mv_tmp.m22 );

			pop_matrix();

			double width = get_engine().canvas.getWidth();

			double height = get_engine().canvas.getHeight();

			aspect = width / height;

			yfov = xfov / aspect;
		}

		double tang = deg.tan( yfov * 0.5 );

		double nh = near * tang;

		double nw = nh * aspect;

		double fh = far * tang;

		double fw = fh * aspect;

		if( render )
		{
			begin_shape( GL_LINES );

			colour( Red );
			draw_vector( position, X );

			colour( Green );
			draw_vector( position, Y );

			colour( Blue );
			draw_vector( position, Z );

			end_shape();
		}

		nearZ.set( Z );

		nearZ.scale( near );

		nc.sum( position, nearZ );

		farZ.set( Z );

		farZ.scale( far );

		fc.sum( position, farZ );

		planes[ near_cp ].n.set( 1, Z );
		planes[ near_cp ].p.set( nc );

		planes[ far_cp ].n.set( -1, Z );
		planes[ far_cp ].p.set( fc );

		aux.set( nc );
		aux.add( nh, Y );
		aux.sub( position );
		aux.normalise();
		normal.cross( aux, X );
		planes[ top_cp ].n.set( normal );
		planes[ top_cp ].p.set( nc );
		planes[ top_cp ].p.add( nh, Y );

		aux.set( nc );
		aux.add( -nh, Y );
		aux.sub( position );
		aux.normalise();
		normal.cross( X, aux );
		planes[ bottom_cp ].n.set( normal );
		planes[ bottom_cp ].p.set( nc );
		planes[ bottom_cp ].p.add( -nh, Y );

		aux.set( nc );
		aux.add( -nw, X );
		aux.sub( position );
		aux.normalise();
		normal.cross( aux, Y );
		planes[ left_cp ].n.set( normal );
		planes[ left_cp ].p.set( nc );
		planes[ left_cp ].p.add( -nw, X );

		aux.set( nc );
		aux.add( nw, X );
		aux.sub( position );
		aux.normalise();
		normal.cross( Y, aux );
		planes[ right_cp ].n.set( normal );
		planes[ right_cp ].p.set( nc );
		planes[ right_cp ].p.add( nw, X );
		* */
	}

	/**
	 * Check to see if the given point in within this camera's view frustum
	 * the point may still be occluded - this check doesn't take that into account
	 */
	public boolean can_potentially_see( Vec3d p )
	{
		for( Plane plane : planes )
		{
			if( distance_between_point_and_plane( p, plane.n, plane.p ) < 0 ) return false;
		}

		return true;
	}

	/**
	 * Check to see if any part of the given sphere in within this camera's view frustum
	 * the sphere may still be occluded - this check doesn't take that into account
	 */
	public boolean can_potentially_see( Vec3d p, double r )
	{
		for( Plane plane : planes )
		{
			if( distance_between_point_and_plane( p, plane.n, plane.p ) < -r ) return false;
		}

		return true;
	}
	
	public void draw_to_middle_buffer()
	{
		glDrawBuffer( GL_BACK );
	}	
	
	public void draw_to_left_buffer()
	{
		glDrawBuffer( GL_BACK_LEFT );
	}
	
	public void draw_to_right_buffer()
	{
		glDrawBuffer( GL_BACK_RIGHT );
	}
	
	public void draw_to_buffer()
	{
		glDrawBuffer( GL_BACK );
	}
	
	
	public void displayGL( RenderingMode mode, double eye_sep, double aspect_factor )
	{
		//cverbose.println( mtrace() );

		double aspect = 0;

		double yfov = 0;
		
		if( active )
		{
			// cverbose.println( "Engine: %s", get_engine() );
			
			// cverbose.println( "Canvas: %s", get_engine().canvas );
			
			int x0 = 0;
			
			int y0 = 0;
			
			int x1 = 0;
			
			int y1 = 0;
			
			if( mode == RenderingMode.Mono )
			{
				x0 = 0;
				y0 = 0;
				
				x1 = get_engine().canvas.getWidth();
				y1 = get_engine().canvas.getHeight();
				
				//aspect_factor = 1.0;
			}
			else if( mode == RenderingMode.LeftEyeHorizontalStereo )
			{
				x0 = 0;
				y0 = 0;
				
				x1 = get_engine().canvas.getWidth() / 2;
				y1 = get_engine().canvas.getHeight();
				
				//aspect_factor = 2.0;
			}
			else if( mode == RenderingMode.RightEyeHorizontalStereo )
			{
				x0 = get_engine().canvas.getWidth() / 2;
				y0 = 0;
				
				x1 = get_engine().canvas.getWidth() / 2;
				y1 = get_engine().canvas.getHeight();
				
				//aspect_factor = 2.0;
			}
			else if( mode == RenderingMode.LeftEyeVerticalStereo )
			{
				x0 = 0;
				y0 = 0;
				
				x1 = get_engine().canvas.getWidth();
				y1 = get_engine().canvas.getHeight() / 2;
				
				//aspect_factor = 0.5;
			}
			else if( mode == RenderingMode.RightEyeVerticalStereo )
			{
				x0 = 0;
				y0 = get_engine().canvas.getHeight() / 2;
				
				x1 = get_engine().canvas.getWidth();
				y1 = get_engine().canvas.getHeight() / 2;
				
				//aspect_factor = 0.5;
			}
			
			//aspect = clip( 0, 0, get_engine().canvas.getWidth(), get_engine().canvas.getHeight() );
			
			aspect = clip( x0, y0, x1, y1 );
			
			aspect *= aspect_factor;

			yfov = xfov / aspect;

			glMatrixMode( GL_PROJECTION );

			glLoadIdentity();

			gluPerspective( yfov, aspect, near, far );

			//glDepthRange( near, far );

			//cverbose.println( "camera: %s, %s, %s", position, target, up );
			
			Vec3d eye_to_target = new Vec3d( target ).sub( position );
			
			Vec3d eye_axis = cross( eye_to_target, up );
			
			Vec3d eye_position = new Vec3d( position );
			
			eye_position.add( eye_sep, eye_axis );

			//camera( position, target, up );
			
			camera( eye_position, target, up );

			get_modelview_matrix( mv_tmp );

			X.set( mv_tmp.m00, mv_tmp.m01, mv_tmp.m02 );
			Y.set( mv_tmp.m10, mv_tmp.m11, mv_tmp.m12 );
			Z.set( -mv_tmp.m20, -mv_tmp.m21, -mv_tmp.m22 );
			
			//Vec3d eye = new Vec3d( position );

			//eye.add( half_eye_sep, X );
		}
		else
		{
			push_matrix();

			camera( position, target, up );

			get_modelview_matrix( mv_tmp );

			X.set( mv_tmp.m00, mv_tmp.m01, mv_tmp.m02 );
			Y.set( mv_tmp.m10, mv_tmp.m11, mv_tmp.m12 );
			Z.set( -mv_tmp.m20, -mv_tmp.m21, -mv_tmp.m22 );

			pop_matrix();

			double width = get_engine().canvas.getWidth();

			double height = get_engine().canvas.getHeight();

			aspect = width / height;

			yfov = xfov / aspect;
		}

		double tang = deg.tan( yfov * 0.5 );

		double nh = near * tang;

		double nw = nh * aspect;

		// double fh = far * tang;

		// double fw = fh * aspect;

		if( render )
		{
			begin_shape( GL_LINES );

			colour( Red );
			draw_vector( position, X );

			colour( Green );
			draw_vector( position, Y );

			colour( Blue );
			draw_vector( position, Z );

			end_shape();
		}

		nearZ.set( Z );

		nearZ.scale( near );

		nc.sum( position, nearZ );

		farZ.set( Z );

		farZ.scale( far );

		fc.sum( position, farZ );

		planes[ near_cp ].n.set( 1, Z );
		planes[ near_cp ].p.set( nc );

		planes[ far_cp ].n.set( -1, Z );
		planes[ far_cp ].p.set( fc );

		aux.set( nc );
		aux.add( nh, Y );
		aux.sub( position );
		aux.normalise();
		normal.cross( aux, X );
		planes[ top_cp ].n.set( normal );
		planes[ top_cp ].p.set( nc );
		planes[ top_cp ].p.add( nh, Y );

		aux.set( nc );
		aux.add( -nh, Y );
		aux.sub( position );
		aux.normalise();
		normal.cross( X, aux );
		planes[ bottom_cp ].n.set( normal );
		planes[ bottom_cp ].p.set( nc );
		planes[ bottom_cp ].p.add( -nh, Y );

		aux.set( nc );
		aux.add( -nw, X );
		aux.sub( position );
		aux.normalise();
		normal.cross( aux, Y );
		planes[ left_cp ].n.set( normal );
		planes[ left_cp ].p.set( nc );
		planes[ left_cp ].p.add( -nw, X );

		aux.set( nc );
		aux.add( nw, X );
		aux.sub( position );
		aux.normalise();
		normal.cross( Y, aux );
		planes[ right_cp ].n.set( normal );
		planes[ right_cp ].p.set( nc );
		planes[ right_cp ].p.add( nw, X );
	}
}
