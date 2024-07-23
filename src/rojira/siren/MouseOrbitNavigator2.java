package rojira.siren;

import static rojira.jsi4.LibGUI.any_mouse;
import static rojira.jsi4.LibMaths.abs;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import rojira.jsi4.util.gui.InputListener;
import rojira.jsi4.util.maths.Vec3d;

public class MouseOrbitNavigator2 extends PolarTranslator2 implements InputListener
{
	public double sx = 1.0f;

	public double sy = 1.0f;

	public double sz = 1.0f;

	private int last_mouseX, last_mouseY;

	public int drag_button = any_mouse;

	// private boolean dragging;


	public MouseOrbitNavigator2()
	{
		this( new Vec3d(), new Vec3d() );
	}


	public MouseOrbitNavigator2( Vec3d target )
	{
		this( target, new Vec3d() );
	}


	public MouseOrbitNavigator2( Vec3d position, Vec3d target )
	{
		super( position, target );
	}


	public void mouse_pressed( MouseEvent e )
	{
	}

	public void mouse_released( MouseEvent e )
	{
	}

	public void mouse_dragged( MouseEvent e )
	{
	}


	public void mouse_moved( MouseEvent e )
	{
		mouse_rotating( e );
	}

	private void mouse_rotating( MouseEvent e )
	{
		int mouseX = e.getX();
		int mouseY = e.getY();

		int delta_x = mouseX - last_mouseX;
		int delta_y = mouseY - last_mouseY;

		if( abs( delta_x ) > 99 || abs( delta_y ) > 99 )
		{
			last_mouseX = mouseX;

			last_mouseY = mouseY;

			return;
		}

		double dx = sx * delta_x;
		double dy = sy * delta_y;

		theta -= dx;

		phi += dy;

		if( phi > 89 ) phi = 89;

		if( phi < -89 ) phi = -89;

		last_mouseX = mouseX;

		last_mouseY = mouseY;
	}

	public void mouse_wheel_moved( MouseWheelEvent e )
	{
		int mouse_wheel = e.getWheelRotation();

		double dz = sz * mouse_wheel;

		rad += dz;
	}

	public void key_typed( char key, int code, KeyEvent e ){};

	public void key_pressed( char key, int code, KeyEvent e ){};

	public void key_released( char key, int code, KeyEvent e ){};

	public void mouse_clicked( MouseEvent e ){};

	public void mouse_entered( MouseEvent e ){};

	public void mouse_exited( MouseEvent e ){};
}
