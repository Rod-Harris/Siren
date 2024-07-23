package rojira.siren;

import static rojira.jsi4.LibGUI.*;

import static rojira.sigl.LibSiGL.*;
import static rojira.sigl.GLWrap.*;

public class CLW extends GLScene
{
	public int clear_colour = Black;

	public int clear_buffers = GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT;

	public CLW()
	{
		this( Black );
	}

	public CLW( int clear_colour )
	{
		this.clear_colour = clear_colour;
	}

	public void initGL()
	{
	}

	public void updateGL( double dt )
	{
	}

	public void displayGL()
	{
		clw( clear_colour, clear_buffers );
	}
}
