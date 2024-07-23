package rojira.siren.ext;

import static rojira.sigl.GLWrap.GL_LINEAR;
import static rojira.sigl.GLWrap.GL_REPLACE;
import static rojira.sigl.LibSiGL.call_list;
import static rojira.sigl.LibSiGL.create_texture;
import static rojira.sigl.LibSiGL.end_list;
import static rojira.sigl.LibSiGL.front_ccw;
import static rojira.sigl.LibSiGL.front_cw;
import static rojira.sigl.LibSiGL.sphere;
import static rojira.sigl.LibSiGL.start_list;

import rojira.sigl.Texture;
import rojira.siren.GLScene;

/**
 @deprecated
 Use SkySphereRenderer instead
 */
@Deprecated
public class SkySphere extends GLScene
{
	String image_path;

	Texture texture;

	int list_no;

	double radius;

	int xtile, ytile;


	public SkySphere( double radius, int xtile, int ytile, String image_path ) throws Exception
	{
		this.radius = radius;

		this.image_path = image_path;

		this.xtile = xtile;

		this.ytile = ytile;
	}


	public void initGL() throws Exception
	{
		texture = create_texture( image_path );

		texture.mode = GL_REPLACE;

		texture.mag_filter = GL_LINEAR;

		texture.min_filter = GL_LINEAR;

		list_no = start_list();

		sphere( 16, 8, radius, xtile, ytile, false, true );

		end_list();
	}


	public void updateGL( double dt )
	{
	}


	public void displayGL()
	{
		front_cw();

		texture.apply();

		call_list( list_no );

		front_ccw();

		texture.unapply();
	}
}
