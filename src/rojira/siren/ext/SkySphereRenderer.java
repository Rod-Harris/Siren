package rojira.siren.ext;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibMaths.deg;
import static rojira.jsi4.LibMaths.pack;
import static rojira.jsi4.LibSystem._string;
import static rojira.sigl.GLWrap.GL_CLAMP;
import static rojira.sigl.GLWrap.GL_LINEAR;
import static rojira.sigl.GLWrap.GL_QUADS;
import static rojira.sigl.LibSiGL.begin_shape;
import static rojira.sigl.LibSiGL.call_list;
import static rojira.sigl.LibSiGL.create_texture;
import static rojira.sigl.LibSiGL.end_list;
import static rojira.sigl.LibSiGL.end_shape;
import static rojira.sigl.LibSiGL.pop_matrix;
import static rojira.sigl.LibSiGL.push_matrix;
import static rojira.sigl.LibSiGL.start_list;
import static rojira.sigl.LibSiGL.vertex;

import rojira.jsi4.util.maths.Vec2d;
import rojira.jsi4.util.maths.Vec3d;
import rojira.sigl.Texture;


public class SkySphereRenderer
{
	String colour_texture_dir_path;

	String colour_texture_name_format;

	SphereSegment[][] segments;

	int tx, ty;

	public SkySphereRenderer( String textures_directory, String textures_name_format, int x_tiles, int y_tiles ) throws Exception
	{
		this.colour_texture_dir_path = textures_directory;

		this.colour_texture_name_format = textures_name_format;

		this.tx = x_tiles;

		this.ty = y_tiles;

		segments = new SphereSegment[ tx ][ ty ];

		for( int x=0; x<tx; x++ )
		{
			for( int y=0; y<ty; y++ )
			{
				cdebug.println( "Rendering tile %d x %d", x, y );

				segments[ x ][ y ] = new SphereSegment();

				SphereSegment segment = segments[ x ][ y ];

				String colour_texture_name = colour_texture_name_format.replace( "${X}", _string( x ) ).replace( "${Y}", _string( y ) );

				cdebug.println( "colour_texture_name = %s", colour_texture_name );

				double min_theta = pack( x, 0, tx, 0, 360 );

				double max_theta = pack( x+1, 0, tx, 0, 360 );

				double min_phi = pack( y, 0, ty, -90, 90 );

				double max_phi = pack( y+1, 0, ty, -90, 90 );

				int xsegs = (int) ( max_theta - min_theta ) / 2;

				int ysegs = (int) ( max_phi - min_phi ) / 2;

				cdebug.println( "min_theta = %f", min_theta );

				cdebug.println( "max_theta = %f", max_theta );

				cdebug.println( "min_phi = %f", min_phi );

				cdebug.println( "max_phi = %f", max_phi );

				cdebug.println( "xsegs = %d", xsegs );

				cdebug.println( "ysegs = %d", ysegs );

				segment.list = start_list();

				render_partial_sphere( 1, min_theta, max_theta, min_phi, max_phi, xsegs, ysegs );

				end_list();

				segment.colour_texture = create_texture( colour_texture_dir_path + "/" + colour_texture_name );

				segment.colour_texture.min_filter = GL_LINEAR;

				segment.colour_texture.mag_filter = GL_LINEAR;

				segment.colour_texture.wrap_s = GL_CLAMP;

				segment.colour_texture.wrap_t = GL_CLAMP;
			}
		}
	}

	/**
	 * Rendering environment requirements
	 * lights: off
	 * textures: on
	 * cull: disabled or clockwise front facing
	 * camera position: 0,0,0
	 * camera fov: 90
	 * depth test: off
	 * depth write: off
	 */
	public void render()
	{
		push_matrix();

		//rotateX( 180 );

		for( int x=0; x<tx; x++ )
		{
			for( int y=0; y<ty; y++ )
			{
				segments[ x ][ y ].colour_texture.apply();

				call_list( segments[ x ][ y ].list );
			}
		}

		pop_matrix();
	}


	static void render_partial_sphere( double radius, double min_theta, double max_theta, double min_phi, double max_phi, int xsegs, int ysegs )
	{
		Vec3d v = new Vec3d();

		Vec2d t = new Vec2d();

		begin_shape( GL_QUADS );

		for( int xseg=0; xseg<xsegs-1; xseg++ )
		{
			for( int yseg=0; yseg<ysegs-1; yseg++ )
			{
				double theta0 = pack( xseg, 0, xsegs-1, min_theta, max_theta );

				double theta1 = pack( xseg+1, 0, xsegs-1, min_theta, max_theta );

				double phi0 = pack( yseg, 0, ysegs-1, min_phi, max_phi );

				double phi1 = pack( yseg+1, 0, ysegs-1, min_phi, max_phi );

				double s0 = pack( xseg, 0, xsegs-1, 0, 1 );

				double s1 = pack( xseg+1, 0, xsegs-1, 0, 1 );

				double t0 = pack( yseg, 0, ysegs-1, 0, 1 );

				double t1 = pack( yseg+1, 0, ysegs-1, 0, 1 );

				deg.polar_to_cartesian( radius, theta0, phi0, v );

				t.x = s0;

				t.y = t0;

				vertex( v, t );

				deg.polar_to_cartesian( radius, theta0, phi1, v );

				t.x = s0;

				t.y = t1;

				vertex( v, t );

				deg.polar_to_cartesian( radius, theta1, phi1, v );

				t.x = s1;

				t.y = t1;

				vertex( v, t );

				deg.polar_to_cartesian( radius, theta1, phi0, v );

				t.x = s1;

				t.y = t0;

				vertex( v, t );
			}
		}

		end_shape();
	}
}


class SphereSegment
{
	int list;

	Texture colour_texture;
}


