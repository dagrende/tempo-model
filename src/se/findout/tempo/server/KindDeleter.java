package se.findout.tempo.server;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class KindDeleter {
	public static int deleteAllOfKind(String kind) {
		int totalDeleted = 0;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		final Query query = new Query(kind);
		query.setKeysOnly();
		while (true) {
			Iterable<Entity> iterable = ds.prepare(query).asIterable(FetchOptions.Builder.withLimit(128));
			final ArrayList<Key> keys = new ArrayList<Key>();
			int n = 0;
			for (Entity entity : iterable) {
				keys.add(entity.getKey());
				n++;
			}
			if (n == 0) {
				break;
			}
			ds.delete(keys);
			totalDeleted += n;
		}
		return totalDeleted;
	}

}
