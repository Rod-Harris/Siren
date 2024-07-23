package rojira.siren.ext;

import static rojira.jsi4.LibGUI.argb;
import static rojira.sigl.GLWrap.GL_BLEND;
import static rojira.sigl.GLWrap.GL_LINEAR;
import static rojira.sigl.GLWrap.GL_ONE;
import static rojira.sigl.GLWrap.GL_REPLACE;
import static rojira.sigl.GLWrap.glBlendFunc;
import static rojira.sigl.GLWrap.glDisable;
import static rojira.sigl.GLWrap.glEnable;
import static rojira.sigl.LibSiGL.call_list;
import static rojira.sigl.LibSiGL.colour;
import static rojira.sigl.LibSiGL.create_texture;
import static rojira.sigl.LibSiGL.cull;
import static rojira.sigl.LibSiGL.depth_test;
import static rojira.sigl.LibSiGL.depth_write;
import static rojira.sigl.LibSiGL.end_list;
import static rojira.sigl.LibSiGL.front_ccw;
import static rojira.sigl.LibSiGL.rotateY;
import static rojira.sigl.LibSiGL.sphere;
import static rojira.sigl.LibSiGL.start_list;
import static rojira.sigl.LibSiGL.textures;

import rojira.sigl.Texture;
import rojira.siren.GLScene;

public class Star2 extends GLScene
{
	String img_filename;

	String img_filename2;

	Texture texture;

	Texture texture2;

	int src_blend;

	int dst_blend;

	int sphere_list;

	public Star2( String img_filename, String img_filename2 ) throws Exception
	{
		this.img_filename = img_filename;

		this.img_filename2 = img_filename2;
	}

	@Override
	public void initGL() throws Exception
	{
		// BufferedImage img = load_image( img_filename );

		texture = create_texture( img_filename );

		texture.mode = GL_REPLACE;

		texture.min_filter = GL_LINEAR;

		texture.mag_filter = GL_LINEAR;

		//texture.mode = GL_MODULATE;

		src_blend = GL_ONE;

		//BufferedImage img2 = darken( load_image( img_filename2 ), -10 );

		texture2 = create_texture( img_filename2 );

		//texture2 = create_texture( filter_image( img2, new SeamlessImageFilter( img2 ) ) );

		texture2.mode = GL_REPLACE;

		texture2.min_filter = GL_LINEAR;

		texture2.mag_filter = GL_LINEAR;

		//texture.mode = GL_MODULATE;

		dst_blend = GL_ONE;

		sphere_list = start_list();

		sphere( 72, 36, 1, false, true );

		end_list();

	}

	double r;

	@Override
	public void updateGL( double dt )
	{
		r += 0.1;
	}

	@Override
	public void displayGL()
	{
		cull( true );

		front_ccw();

		textures( true );

		texture.apply();

		depth_test( false );

		depth_write( false );

		//blend( true );

		glDisable( GL_BLEND );

		colour( argb( 0.5, 1, 1, 0.5 ) );

		rotateY( -r );

		//sphere( 32, 16, 1, false, true );

		call_list( sphere_list );

		glEnable( GL_BLEND );

		//glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

		glBlendFunc( src_blend, dst_blend );

		depth_test( true );

		depth_write( true );

		//rotateX( 180 );

		rotateY( 2*r );

		texture2.apply();

		//sphere( 32, 16, 1, false, true );

		call_list( sphere_list );

	}
}
