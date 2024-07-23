package rojira.siren;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


public class MonCamera2 extends Camera implements UIListener
{
	public final MouseOrbitNavigator2 mon;

	public MonCamera2()
	{
		mon = new MouseOrbitNavigator2( super.position, super.target );

		mon.rad = 1;
	}


	public void updateGL( double dt )
	{
		mon.update();
	}


	public void mouse_pressed( MouseEvent e )
	{
		mon.mouse_pressed( e );
	}

	public void mouse_released( MouseEvent e )
	{
		mon.mouse_released( e );
	}

	public void mouse_dragged( MouseEvent e )
	{
		mon.mouse_dragged( e );
	}


	public void mouse_moved( MouseEvent e )
	{
		mon.mouse_moved( e );
	}


	public void mouse_wheel_moved( MouseWheelEvent e )
	{
		mon.mouse_wheel_moved( e );
	}

	public void key_typed( char key, int code, KeyEvent e ){};

	public void key_pressed( char key, int code, KeyEvent e ){};

	public void key_released( char key, int code, KeyEvent e ){};

	public void mouse_clicked( MouseEvent e ){};

	public void mouse_entered( MouseEvent e ){};

	public void mouse_exited( MouseEvent e ){};
}
