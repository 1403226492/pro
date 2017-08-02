package book.database;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.apache.commons.lang3.StringUtils;

public class MongoDBUtils {
	private static final String host = "123.56.30.88";  
	private static final int port = 27017;  
	private static final String userName = "";  
	private static final String password = "";  
	private static final String dataBaseName = "souhu";  
	private static final String tableName = "souhu";
	public static DBCollection connMongoDB() throws Exception {  
	    Mongo mongo = new Mongo(host, port);  
	    DB db = mongo.getDB(dataBaseName);  
	    if (!StringUtils.isEmpty(userName) || !StringUtils.isEmpty(password)) {  
	        db.authenticate(userName, password.toCharArray());  
	    }  
	    DBCollection dbCollection = db.getCollection(tableName);  
	    return dbCollection;
	}
}
