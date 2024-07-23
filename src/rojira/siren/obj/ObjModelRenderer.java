package rojira.siren.obj;

import static rojira.jsi4.LibGUI.Blue;
import static rojira.jsi4.LibGUI.White;
import static rojira.jsi4.LibGUI.argb;
import static rojira.jsi4.LibGUI.argbBd;
import static rojira.jsi4.LibGUI.argbGd;
import static rojira.jsi4.LibGUI.argbRd;
import static rojira.jsi4.LibGUI.create_argb_image;
import static rojira.jsi4.LibGUI.rgb;
import static rojira.jsi4.LibGUI.set_pixel;
import static rojira.sigl.GLWrap.GL_LINES;
import static rojira.sigl.GLWrap.GL_TRIANGLES;
import static rojira.sigl.LibSiGL.ambient_and_diffuse_material;
import static rojira.sigl.LibSiGL.apply_texture;
import static rojira.sigl.LibSiGL.begin_shape;
import static rojira.sigl.LibSiGL.colour;
import static rojira.sigl.LibSiGL.create_texture;
import static rojira.sigl.LibSiGL.draw_vector;
import static rojira.sigl.LibSiGL.emissive_material;
import static rojira.sigl.LibSiGL.end_shape;
import static rojira.sigl.LibSiGL.normal;
import static rojira.sigl.LibSiGL.shininess;
import static rojira.sigl.LibSiGL.specular_material;
import static rojira.sigl.LibSiGL.tex_coord;
import static rojira.sigl.LibSiGL.vertex;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import rojira.jsi4.util.maths.Vec2d;
import rojira.jsi4.util.maths.Vec3d;
import rojira.sigl.Texture;
import rojira.siren.GLRenderer;


public class ObjModelRenderer implements GLRenderer
{
	public final Vec3d min_bound = new Vec3d();

	public final Vec3d max_bound = new Vec3d();

	public final Vec3d middle = new Vec3d();

	public final Vec3d extent = new Vec3d();

	public ArrayList<Vec3d> verticies = new ArrayList<Vec3d>();

	public ArrayList<Vec3d> normals = new ArrayList<Vec3d>();

	public ArrayList<Vec2d> tex_coords = new ArrayList<Vec2d>();

	public ArrayList<Group> groups = new ArrayList<Group>();

	public Map<String,Material> materials;

	public int total_faces;

	public void initGL()
	{
		for( Material mat : materials.values() ) mat.initGL();

		min_bound.set( verticies.get( 0 ) );

		max_bound.set( verticies.get( 0 ) );

		for( Vec3d v : verticies )
		{
			if( v.x < min_bound.x ) min_bound.x = v.x;

			if( v.y < min_bound.y ) min_bound.y = v.y;

			if( v.z < min_bound.z ) min_bound.z = v.z;

			if( v.x > max_bound.x ) max_bound.x = v.x;

			if( v.y > max_bound.y ) max_bound.y = v.y;

			if( v.z > max_bound.z ) max_bound.z = v.z;
		}

		middle.x = ( min_bound.x + max_bound.x ) / 2;

		middle.y = ( min_bound.y + max_bound.y ) / 2;

		middle.z = ( min_bound.z + max_bound.z ) / 2;

		extent.x = ( max_bound.x - middle.x ) * 2;

		extent.y = ( max_bound.y - middle.y ) * 2;

		extent.z = ( max_bound.z - middle.z ) * 2;
	}

	public void updateGL( double dt )
	{
	}

	public void displayGL()
	{
		render_immediate();
	}

	public void render_immediate()
	{
		for( Group group : groups )
		{
			materials.get( group.material_name ).apply();

			begin_shape( GL_TRIANGLES );

			for( Face face : group.faces )
			{
				for( VTNIndex vtn_index: face.indicies )
				{
					if( vtn_index.normal != -1 )
					{
						normal( normals.get( vtn_index.normal ) );
					}
					if( vtn_index.tex_coord != -1 )
					{
						tex_coord( tex_coords.get( vtn_index.tex_coord ) );
					}

					vertex( verticies.get( vtn_index.vertex ) );
				}
			}

			end_shape();
		}
	}

	public void render_bounds()
	{
	}

	public void render_normals()
	{
		colour( Blue );

		begin_shape( GL_LINES );

		for( Group group : groups )
		{
			for( Face face : group.faces )
			{
				for( VTNIndex vtn_index: face.indicies )
				{
					if( vtn_index.normal != -1 )
					{
						draw_vector( verticies.get( vtn_index.vertex ), normals.get( vtn_index.normal ) );

						//cout.println( "draw vector %s + %s", verticies.get( vtn_index.vertex ), normals.get( vtn_index.normal ) );
					}
				}
			}
		}

		end_shape();
	}
}



class Material
{
	public int ambient_and_diffuse = rgb( 0.6, 0.6, 0.6 );

	public int specular = rgb( 1.0, 1.0, 1.0 );
	
	public int emissive = rgb( 0.0, 0.0, 0.0 );

	public double shininess = 0;

	public double alpha = 1.0;

	public BufferedImage diffuse_img;

	public Texture diffuse_texture;

	static Material default_material = new Material();

	public static Texture dummy_texture;
	

	static
	{
		default_material.init();
	}


	public void init()
	{
		ambient_and_diffuse = argb( alpha, argbRd( ambient_and_diffuse ), argbGd( ambient_and_diffuse ), argbBd( ambient_and_diffuse ) );

		specular = argb( alpha, argbRd( specular ), argbGd( specular ), argbBd( specular ) );
		
		emissive = argb( alpha, argbRd( emissive ), argbGd( emissive ), argbBd( emissive ) );
	}


	public void initGL()
	{
		if( dummy_texture == null )
		{
			BufferedImage dummy_img = create_argb_image( 1, 1 );
			
			set_pixel( dummy_img, 0, 0, White );
			
			dummy_texture = create_texture( dummy_img, 0 );
		}
		
		if( diffuse_img != null )
		{
			diffuse_texture = create_texture( diffuse_img, ObjLoader.mipmap_level );

			diffuse_img = null;

			diffuse_texture.mode = ObjLoader.texture_mode;
			
			diffuse_texture.min_filter = ObjLoader.texture_min_filter;
			
			diffuse_texture.mag_filter = ObjLoader.texture_mag_filter;
		}
		else
		{
			diffuse_texture = dummy_texture;
		}
	}


	public void apply()
	{
		colour( ambient_and_diffuse );

		ambient_and_diffuse_material( ambient_and_diffuse );

		specular_material( specular );
		
		emissive_material( emissive );		
		
		shininess( shininess );

		apply_texture( diffuse_texture );
	}
}
