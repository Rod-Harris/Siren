package rojira.siren.obj;

import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibGUI.argbA;
import static rojira.jsi4.LibGUI.argbB;
import static rojira.jsi4.LibGUI.argbG;
import static rojira.jsi4.LibGUI.argbR;
import static rojira.jsi4.LibGUI.load_image;
import static rojira.jsi4.LibGUI.rgb;
import static rojira.jsi4.LibIO.read_file;
import static rojira.jsi4.LibSystem._double;
import static rojira.jsi4.LibSystem._int;
import static rojira.jsi4.LibText.fmt;
import static rojira.sigl.GLWrap.GL_MODULATE;
import static rojira.sigl.GLWrap.GL_NEAREST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import rojira.jsi4.util.maths.Vec2d;
import rojira.jsi4.util.maths.Vec3d;


public class ObjLoader
{
	public static ArrayList<Exception> problems = new ArrayList<Exception>();


	public static int mipmap_level = 0;
	
	public static int texture_mode = GL_MODULATE;
	
	public static int texture_min_filter = GL_NEAREST;
	
	public static int texture_mag_filter = GL_NEAREST;
	

	public static ObjModelRenderer parse( File f ) throws FileNotFoundException, IOException
	{
		ObjModelRenderer model = new ObjModelRenderer();

		Group group = null;

		String data = read_file( f );

		int line_no = 0;

		for( String line : data.split( "\n" ) )
		{
			line_no ++;

			String[] tokens = line.split( "\\s+" );

			if( tokens.length < 1 ) continue;

			String key = tokens[ 0 ];

			if( key.startsWith( "#" ) )
			{
				continue;
			}
			else if( "g".equals( key ) )
			{
				group = new Group();

				group.name = tokens[ 1 ];

				model.groups.add( group );
			}
			else if( "usemtl".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a usemtl: tokens length != 2", f.getPath(), line_no, line ) ) );

					continue;
				}

				group.material_name = tokens[ 1 ];
			}
			else if( "v".equals( key ) )
			{
				if( tokens.length != 4 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Vec3d: tokens length != 4", f.getPath(), line_no, line ) ) );

					continue;
				}

				double x = _double( tokens[ 1 ] );

				double y = _double( tokens[ 2 ] );

				double z = _double( tokens[ 3 ] );

				model.verticies.add( new Vec3d( x, y, z ) );
			}
			else if( "vt".equals( key ) )
			{
				if( tokens.length != 3 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Vec2d: tokens length != 3", f.getPath(), line_no, line ) ) );

					continue;
				}

				double u = _double( tokens[ 1 ] );

				double v = _double( tokens[ 2 ] );

				model.tex_coords.add( new Vec2d( u, v ) );
			}
			else if( "vn".equals( key ) )
			{
				if( tokens.length != 4 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Vec3d: tokens length != 4", f.getPath(), line_no, line ) ) );

					continue;
				}

				double x = _double( tokens[ 1 ] );

				double y = _double( tokens[ 2 ] );

				double z = _double( tokens[ 3 ] );

				model.normals.add( new Vec3d( x, y, z ) );
			}
			else if( "f".equals( key ) )
			{
				Face face = new Face();

				if( tokens.length != 4 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Face: tokens length != 4", f.getPath(), line_no, line ) ) );

					continue;
				}

				for( int i=1; i<tokens.length; i++ )
				{
					String[] tokens2 = tokens[ i ].split( "/" );

					//cout.println( "face token length = %d", tokens2.length );

					VTNIndex vtn_index = new VTNIndex();

					vtn_index.vertex = _int( tokens2[ 0 ] ) -1;

					if( tokens2.length > 1 )
					{
						if( tokens2[ 1 ].length() != 0 )
						{
							vtn_index.tex_coord = _int( tokens2[ 1 ] ) -1;
						}

						if( tokens2[ 2 ].length() != 0 )
						{
							vtn_index.normal = _int( tokens2[ 2 ] ) -1;
						}
					}

					face.indicies.add( vtn_index );
				}

				group.faces.add( face );

				model.total_faces ++;
			}
			else if( "mtllib".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a mtllib: tokens length != 2", f.getPath(), line_no, line ) ) );

					continue;
				}

				model.materials = parse_mtl_file( new File( f.getParent() + "/" + tokens[ 1 ] ) );
			}
			else
			{
				problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' unknown line definition", f.getPath(), line_no, line ) ) );

				continue;
			}
		}

		cverbose.println( "Obj Model Parser:" );

		cverbose.println( "   Groups: %d", model.groups.size() );

		for( Group _group : model.groups )
		{
			cverbose.println( "   Group: %s", _group.name );

			cverbose.println( "      Faces: %d", _group.faces.size() );

			cverbose.println( "      Material: %s", _group.material_name );
			
			cverbose.println( "      model: %s", model );
			
			cverbose.println( "      model.materials: %s", model.materials );

			Material mat = model.materials.get( _group.material_name );

			cverbose.println( "         Ambient & Diffuse: %d-%d-%d-%d", argbA( mat.ambient_and_diffuse ), argbR( mat.ambient_and_diffuse ), argbG( mat.ambient_and_diffuse ), argbB( mat.ambient_and_diffuse ) );

			cverbose.println( "         Specular: %d-%d-%d-%d", argbA( mat.specular ), argbR( mat.specular ), argbG( mat.specular ), argbB( mat.specular ) );

			cverbose.println( "         Emissive: %d-%d-%d-%d", argbA( mat.emissive ), argbR( mat.emissive ), argbG( mat.emissive ), argbB( mat.emissive ) );
		}

		return model;
	}

	private static Map<String,Material> parse_mtl_file( File file ) throws FileNotFoundException, IOException
	{
		Map<String,Material> map = new HashMap<String,Material>();

		String data = read_file( file );

		Material mtl = null;

		int line_no = 0;

		for( String line : data.split( "\n" ) )
		{
			line_no ++;

			String[] tokens = line.split( "\\s+" );

			if( tokens.length < 1 ) continue;

			String key = tokens[ 0 ];

			if( key.startsWith( "#" ) )
			{
				continue;
			}
			else if( "newmtl".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a newmtl: tokens length != 2", file.getPath(), line_no, line ) ) );

					continue;
				}

				mtl = new Material();

				map.put( tokens[ 1 ], mtl );

				continue;
			}
			else if( "Kd".equals( key ) )
			{
				if( tokens.length != 4 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Colour3d: tokens length != 4", file.getPath(), line_no, line ) ) );

					continue;
				}

				double r = _double( tokens[ 1 ] );

				double g = _double( tokens[ 2 ] );

				double b = _double( tokens[ 3 ] );

				mtl.ambient_and_diffuse = rgb( r, g, b );
			}
			else if( "Ks".equals( key ) )
			{
				if( tokens.length != 4 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Colour3d: tokens length != 4", file.getPath(), line_no, line ) ) );

					continue;
				}

				double r = _double( tokens[ 1 ] );

				double g = _double( tokens[ 2 ] );

				double b = _double( tokens[ 3 ] );

				mtl.specular = rgb( r, g, b );
			}
			else if( "Ka".equals( key ) )
			{
				if( tokens.length != 4 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a Colour3d: tokens length != 4", file.getPath(), line_no, line ) ) );

					continue;
				}

				double r = _double( tokens[ 1 ] );

				double g = _double( tokens[ 2 ] );

				double b = _double( tokens[ 3 ] );

				mtl.emissive = rgb( r, g, b );
			}
			else if( "d".equals( key ) || "Tr".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a transparency: tokens length != 2", file.getPath(), line_no, line ) ) );

					continue;
				}

				mtl.alpha = _double( tokens[ 1 ] );
			}
			else if( "Ns".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as a shininess: tokens length != 2", file.getPath(), line_no, line ) ) );

					continue;
				}

				mtl.shininess = cap( _double( tokens[ 1 ] ) / 128.0, 0, 1 );
			}
			else if( "map_Kd".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' as an image path: tokens length != 2", file.getPath(), line_no, line ) ) );

					continue;
				}


				mtl.diffuse_img = load_image( file.getParent() + "/" + tokens[ 1 ] );
			}
			else if( "map_Ks".equals( key ) )
			{
				if( tokens.length != 2 )
				{
					problems.add( new DataFormatException( fmt( "%s:%d Loader doesn't handle multi-texturing (specular image)", file.getPath(), line_no, line ) ) );

					continue;
				}
			}
			else
			{
				problems.add( new DataFormatException( fmt( "%s:%d couldn't parse '%s' unknown line definition", file.getPath(), line_no, line ) ) );

				continue;
			}
		}

		for( Material mat : map.values() )
		{
			mat.init();
		}

		map.put( "default", Material.default_material );

		return map;
	}
	
	
	private static double cap( double value, double min, double max )
	{
		if( value < min ) return min;
		
		if( value > max ) return max;
		
		return value;
	}
}
