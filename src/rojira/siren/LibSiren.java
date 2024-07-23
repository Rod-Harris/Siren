package rojira.siren;

public class LibSiren
{
	/**
	public static final int BUILD = 288;

	public static final long BUILD_TIME = 1326079407299L;

	public static String[] args;

	public static void main( String[] args ) throws Exception
	{
		LibSiren.args = args;

		if( args.length == 1 && "--version".equals( args[ 0 ] ) )
		{
			version( 0 );
		}

		else if( args.length == 1 && "--copy".equals( args[ 0 ] ) )
		{
			show_copyrights();
		}

		else if( args.length == 1 && "--unit".equals( args[ 0 ] ) )
		{
			run_unit_tests();
		}

		else if( args.length == 2 && "--test-skybox".equals( args[ 0 ] ) )
		{
			new rojira.siren.tests.SkyBoxTest( args[ 1 ] );
		}

		else if( args.length == 2 && "--test-star".equals( args[ 0 ] ) )
		{
			new rojira.siren.tests.StarTest( args[ 1 ] );
		}

		else if( args.length == 3 && "--test-star2".equals( args[ 0 ] ) )
		{
			new rojira.siren.tests.StarTest2( args[ 1 ], args[ 2 ] );
		}

		else
		{
			usage( 0 );
		}
	}



	@Test
	public void test1()
	{
	}




	public static void version( int exit_code )
	{
		System.err.println( "LibSiren build " + BUILD + " on " + new java.util.Date( BUILD_TIME ) );

		if( exit_code > 0 ) return;

		System.exit( exit_code );
	}


	static void show_copyrights() throws Exception
	{
		for( String line : load_text_resource( LibSiren.class, "copyrights/list.txt" ).split( "\n" ) )
		{
			if( no_data( line ) ) continue;

			line = line.trim();

			if( line.startsWith( "#" ) ) continue;

			String[] tokens = line.split( " : " );

			if( tokens.length != 2 ) continue;

			System.out.println( "\n" + tokens[ 0 ] );

			//System.out.println( "\n" + tokens[ 1 ] );

			System.out.println( "\n" + load_text_resource( LibSiren.class, "copyrights/" + tokens[ 1 ].trim() ) );
		}

		System.exit( 0 );
	}


	static void usage( int exit_code )
	{
		System.err.println( "usage: LibSiren [options]" );

		System.err.println( "   --version      :  prints build number and date" );
		System.err.println( "   --copy         :  copyright info for this library and all included dependencies" );
		System.err.println( "   --unit         :  runs units tests" );
		System.err.println();
		System.err.println( "   --test-skybox [maps_dir]  :  runs skybox example" );
		System.err.println( "   --test-star   [image]     :  runs star example" );

		if( exit_code > 0 ) return;

		System.exit( exit_code );
	}
	*/
}
