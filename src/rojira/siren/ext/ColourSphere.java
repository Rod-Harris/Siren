package rojira.siren.ext;

import static rojira.sigl.GLUTWrap.glutSolidSphere;
import static rojira.sigl.LibSiGL.colour;
import static rojira.sigl.LibSiGL.pop_matrix;
import static rojira.sigl.LibSiGL.push_matrix;
import static rojira.sigl.LibSiGL.rotateX;
import static rojira.sigl.LibSiGL.translate;

import rojira.jsi4.util.maths.Vec3d;
import rojira.siren.GLRenderer;


public class ColourSphere implements GLRenderer
{
	public final Vec3d pos = new Vec3d();

	public double pitch;

	public int colour;

	public void initGL()
	{
	}

	public void updateGL( double dt )
	{
	}

	public void displayGL()
	{
		push_matrix();

		translate( pos );

		rotateX( pitch );

		colour( colour );

		glutSolidSphere( 1, 16, 16 );

		pop_matrix();
	}
}
