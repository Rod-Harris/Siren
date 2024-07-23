package rojira.siren.ext;

import static rojira.jsi4.LibText.fmt;
import static rojira.sigl.GLWrap.GL_LINEAR;
import static rojira.sigl.GLWrap.GL_LINEAR_MIPMAP_LINEAR;
import static rojira.sigl.GLWrap.GL_QUADS;
import static rojira.sigl.GLWrap.GL_REPLACE;
import static rojira.sigl.LibSiGL.begin_shape;
import static rojira.sigl.LibSiGL.create_texture;
import static rojira.sigl.LibSiGL.end_shape;
import static rojira.sigl.LibSiGL.pop_matrix;
import static rojira.sigl.LibSiGL.push_matrix;
import static rojira.sigl.LibSiGL.translate;
import static rojira.sigl.LibSiGL.vertex;

import java.io.File;

import rojira.jsi4.util.maths.Vec2d;
import rojira.jsi4.util.maths.Vec3d;
import rojira.sigl.Texture;
import rojira.siren.GLScene;


public class SkyBox extends GLScene
{
	Texture[] textures = new Texture[ 6 ];

	Vec3d[][] vertices = new Vec3d[ 6 ][ 4 ];

	Vec2d[][] texcoords = new Vec2d[ 6 ][ 4 ];

	String[] texture_filenames;
	
	public Vec3d pos = new Vec3d();


	public SkyBox( double half_span, String front_tex_filename, String right_tex_filename, String back_tex_filename, String left_tex_filename, String bottom_tex_filename, String top_tex_filename ) throws Exception
	{
		this( half_span, new String[]
		{
			front_tex_filename, right_tex_filename, back_tex_filename, left_tex_filename, bottom_tex_filename, top_tex_filename
		} );
	}


	public SkyBox( double half_span, String... texture_filenames ) throws Exception
	{
		if( texture_filenames == null ) throw new NullPointerException( "texture_filenames array is null" );

		if( texture_filenames.length != 6 ) throw new NullPointerException( "texture_filenames array must contain the paths to 6 images" );

		for( String texture_filename : texture_filenames )
		{
			if( ! new File( texture_filename ).exists() ) throw new Exception( fmt( "Skybox image %s doesn't exist", texture_filename ) );
		}

		this.texture_filenames = texture_filenames;

		double s = half_span;

		vertices[ 0 ][ 0 ] = new Vec3d(  s, -s, -s ); texcoords[ 0 ][ 0 ] = new Vec2d( 1, 1 );
		vertices[ 0 ][ 1 ] = new Vec3d( -s, -s, -s ); texcoords[ 0 ][ 1 ] = new Vec2d( 0, 1 );
		vertices[ 0 ][ 2 ] = new Vec3d( -s,  s, -s ); texcoords[ 0 ][ 2 ] = new Vec2d( 0, 0 );
		vertices[ 0 ][ 3 ] = new Vec3d(  s,  s, -s ); texcoords[ 0 ][ 3 ] = new Vec2d( 1, 0 );

		vertices[ 1 ][ 0 ] = new Vec3d(  s, -s,  s ); texcoords[ 1 ][ 0 ] = new Vec2d( 1, 1 );
		vertices[ 1 ][ 1 ] = new Vec3d(  s, -s, -s ); texcoords[ 1 ][ 1 ] = new Vec2d( 0, 1 );
		vertices[ 1 ][ 2 ] = new Vec3d(  s,  s, -s ); texcoords[ 1 ][ 2 ] = new Vec2d( 0, 0 );
		vertices[ 1 ][ 3 ] = new Vec3d(  s,  s,  s ); texcoords[ 1 ][ 3 ] = new Vec2d( 1, 0 );

		vertices[ 2 ][ 0 ] = new Vec3d( -s, -s,  s ); texcoords[ 2 ][ 0 ] = new Vec2d( 1, 1 );
		vertices[ 2 ][ 1 ] = new Vec3d(  s, -s,  s ); texcoords[ 2 ][ 1 ] = new Vec2d( 0, 1 );
		vertices[ 2 ][ 2 ] = new Vec3d(  s,  s,  s ); texcoords[ 2 ][ 2 ] = new Vec2d( 0, 0 );
		vertices[ 2 ][ 3 ] = new Vec3d( -s,  s,  s ); texcoords[ 2 ][ 3 ] = new Vec2d( 1, 0 );

		vertices[ 3 ][ 0 ] = new Vec3d( -s, -s, -s ); texcoords[ 3 ][ 0 ] = new Vec2d( 1, 1 );
		vertices[ 3 ][ 1 ] = new Vec3d( -s, -s,  s ); texcoords[ 3 ][ 1 ] = new Vec2d( 0, 1 );
		vertices[ 3 ][ 2 ] = new Vec3d( -s,  s,  s ); texcoords[ 3 ][ 2 ] = new Vec2d( 0, 0 );
		vertices[ 3 ][ 3 ] = new Vec3d( -s,  s, -s ); texcoords[ 3 ][ 3 ] = new Vec2d( 1, 0 );

		vertices[ 4 ][ 0 ] = new Vec3d( -s, -s, -s ); texcoords[ 4 ][ 0 ] = new Vec2d( 0, 0 );
		vertices[ 4 ][ 1 ] = new Vec3d(  s, -s, -s ); texcoords[ 4 ][ 1 ] = new Vec2d( 1, 0 );
		vertices[ 4 ][ 2 ] = new Vec3d(  s, -s,  s ); texcoords[ 4 ][ 2 ] = new Vec2d( 1, 1 );
		vertices[ 4 ][ 3 ] = new Vec3d( -s, -s,  s ); texcoords[ 4 ][ 3 ] = new Vec2d( 0, 1 );

		vertices[ 5 ][ 0 ] = new Vec3d(  s,  s, -s ); texcoords[ 5 ][ 0 ] = new Vec2d( 1, 1 );
		vertices[ 5 ][ 1 ] = new Vec3d( -s,  s, -s ); texcoords[ 5 ][ 1 ] = new Vec2d( 0, 1 );
		vertices[ 5 ][ 2 ] = new Vec3d( -s,  s,  s ); texcoords[ 5 ][ 2 ] = new Vec2d( 0, 0 );
		vertices[ 5 ][ 3 ] = new Vec3d(  s,  s,  s ); texcoords[ 5 ][ 3 ] = new Vec2d( 1, 0 );
	}


	@Override
	public void initGL() throws Exception
	{
		/*
		for( int f=0; f<6; f++ )
		{
			textures[ f ] = create_texture( texture_filenames[ f ] );

			textures[ f ].mode = GL_REPLACE;

			textures[ f ].mag_filter = GL_LINEAR;

			textures[ f ].min_filter = GL_LINEAR;
		}
		*/
		
		for( int f=0; f<6; f++ )
		{
			textures[ f ] = create_texture( texture_filenames[ f ], -1 );

			textures[ f ].mode = GL_REPLACE;

			textures[ f ].anisotropy_filter_level = 1;
			
			textures[ f ].min_filter = GL_LINEAR_MIPMAP_LINEAR;
			
			textures[ f ].mag_filter = GL_LINEAR;

		}
	}

	@Override
	public void updateGL( double dt )
	{
	}


	@Override
	public void displayGL()
	{
		//cinfo.println( "displayGL" );
		
		push_matrix();
		
		translate( pos );

		for( int f=0; f<6; f++ )
		{
			textures[ f ].apply();

			begin_shape( GL_QUADS );

			for( int v=0; v<4; v++ )
			{
				vertex( vertices[ f ][ v ], texcoords[ f ][ v ] );
			}

			end_shape();

			textures[ f ].unapply();
		}
		
		pop_matrix();
	}
}
