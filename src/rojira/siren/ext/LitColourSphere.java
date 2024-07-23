package rojira.siren.ext;

import static rojira.jsi4.LibGUI.DarkGray;
import static rojira.jsi4.LibGUI.White;
import static rojira.jsi4.LibMaths.deg;
import static rojira.jsi4.LibSystem.systime;
import static rojira.sigl.LibSiGL.ambient_light;
import static rojira.sigl.LibSiGL.antialias;
import static rojira.sigl.LibSiGL.colour;
import static rojira.sigl.LibSiGL.cull;
import static rojira.sigl.LibSiGL.depth_test;
import static rojira.sigl.LibSiGL.lights;
import static rojira.sigl.LibSiGL.point_light;
import static rojira.sigl.LibSiGL.pop_matrix;
import static rojira.sigl.LibSiGL.push_matrix;
import static rojira.sigl.LibSiGL.rotateX;
import static rojira.sigl.LibSiGL.sphere;
import static rojira.sigl.LibSiGL.translate;

import rojira.jsi4.util.maths.Vec3d;
import rojira.siren.GLScene;


public class LitColourSphere extends GLScene
{
	public final Vec3d pos = new Vec3d();

	public final Vec3d light_pos = new Vec3d();

	public double pitch;

	public int colour;


	public void initGL()
	{
	}

	public void updateGL( double dt )
	{
		//degrees();

		long t = ( systime() % 3600 ) / 10;

		//cout.println( t );

		light_pos.x = 10 * deg.cos( t );

		light_pos.z = 10 * deg.sin( t );
	}

	public void displayGL()
	{
		cull( true );

		lights( true );

		antialias( true );

		depth_test( true );

		ambient_light( DarkGray );

		point_light( White, light_pos.x, light_pos.y, light_pos.z);

		push_matrix();

		translate( pos );

		rotateX( pitch );

		colour( colour );

		//glutSolidSphere( 1, 32, 16 );

		sphere( 16, 16, 1, 1, 1, true, false );

		//sphere( int xsegs, int ysegs, double radius, int xtile, int ytile, boolean normals, boolean texcoords )

		pop_matrix();
	}
}
