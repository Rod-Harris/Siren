package rojira.siren;

import rojira.jsi4.util.maths.Vec3d;

public class BSplineInterpolator
{
	private  BSplineInterpolator()
	{
	}

	/**
	 * Calls catmullrom with same parameters
	 */
	public static void bspline_interpolate( Vec3d r, double u, Vec3d c0, Vec3d c1, Vec3d c2, Vec3d c3 )
	{
		catmullrom( r, u, c0, c1, c2, c3 );
	}

	/**
	 * 
	 * @param r result to put the interpolated vector into
	 * @param u 0 -1 (0=c0 ... 1=c3)
	 * @param c0 control-point-0 (start point)
	 * @param c1 control-point-1 (intermediate point)
	 * @param c2 control-point-2 (intermediate point)
	 * @param c3 control-point-3 (finish point)
	 */
	public static void catmullrom( Vec3d r, double u, Vec3d c0, Vec3d c1, Vec3d c2, Vec3d c3 )
	{
		double u2 = u * u;
		double u3 = u2 * u;
		
		double P0, P1, P2, P3;
		
		P0 = c0.x;
		P1 = c1.x;
		P2 = c2.x;
		P3 = c3.x;
		r.x = 0.5 * ( ( 2 * P1 ) + ( -P0 + P2 ) * u + ( 2*P0 - 5*P1 + 4*P2 - P3 ) * u2 + ( -P0 + 3*P1- 3*P2 + P3 ) * u3 );
		
		P0 = c0.y;
		P1 = c1.y;
		P2 = c2.y;
		P3 = c3.y;
		r.y = 0.5 * ( ( 2 * P1 ) + ( -P0 + P2 ) * u + ( 2*P0 - 5*P1 + 4*P2 - P3 ) * u2 + ( -P0 + 3*P1- 3*P2 + P3 ) * u3 );
		
		P0 = c0.z;
		P1 = c1.z;
		P2 = c2.z;
		P3 = c3.z;
		r.z = 0.5 * ( ( 2 * P1 ) + ( -P0 + P2 ) * u + ( 2*P0 - 5*P1 + 4*P2 - P3 ) * u2 + ( -P0 + 3*P1- 3*P2 + P3 ) * u3 );
	}
}
