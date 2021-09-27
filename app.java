import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class app {

	public static void main(String[] args) throws SQLException, UnknownHostException, InterruptedException {

		if(args.length == 0 ) {
			System.out.println("\nargument 1: drop == drop table\nargument 2: number of seconds to run. Default is 100\nExample: java -cp <classpath> drop 1000\nExample: java -cp <classpath> keep 1000\n");
			System.exit(0);
		}
		
		Statement createStmt = null;
		Statement dropStmt = null;
		PreparedStatement queryStmt = null;
		ResultSet rset = null;
		String hostname = null;
		int startKey = 0;
		int seconds = 100;
		hostname = InetAddress.getLocalHost().getHostName();
		
		Connection con = DriverManager.getConnection("jdbc:phoenix:");
		
		if(args[0].equals("drop")) {
			createStmt = con.createStatement();		
			dropStmt = con.createStatement();
			dropStmt.execute("DROP TABLE IF EXISTS app");
			createStmt.executeUpdate("CREATE TABLE app (mykey varchar not null primary key, mycolumn integer)");		
			con.commit();
			createStmt.close();
			dropStmt.close();
		} else {
			Statement startStmt = con.createStatement();
			ResultSet startKeyRs = startStmt.executeQuery("SELECT MAX(mycolumn) as maxKey FROM APP");
			while(startKeyRs.next())
				startKey = startKeyRs.getInt("maxKey") + 1;
			System.out.println("START KEY: " + startKey);
			
		}
	

		if(args.length > 1)
			seconds = Integer.parseInt(args[1]);

		try (PreparedStatement pstmt = con.prepareStatement("UPSERT INTO APP values(?,?)")) {
			System.out.println("WRITING TO TABLE APP");
			
			for (int i = startKey; i < startKey + seconds; i++) {
				String val1 = hostname + ":::" + i;
				pstmt.setString(1, val1);
				pstmt.setInt(2, i);
				pstmt.executeUpdate();
				con.commit();

				String query = "SELECT * FROM APP WHERE mycolumn =?";
				queryStmt = con.prepareStatement(query);
				queryStmt.setInt(1, i);
				rset = queryStmt.executeQuery();
				while(rset.next())
					System.out.println("ROW KEY: " + rset.getString("mykey") + " ROW VALUE: " + rset.getInt("mycolumn"));
				Thread.sleep(1000);
			}
		pstmt.close();
		}
		
		queryStmt.close();
		con.close();
		System.out.println("FINISHED");
	}
}
