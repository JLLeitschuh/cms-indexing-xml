package se.simonsoft.cms.indexing.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.repos.indexing.IndexingDoc;
import se.repos.indexing.item.ItemPathinfo;
import se.repos.indexing.item.ItemProperties;
import se.simonsoft.cms.item.CmsItemPath;
import se.simonsoft.cms.item.CmsRepository;
import se.simonsoft.cms.item.RepoRevision;

/**
 * Preprocesses the doc for common fields so that the old reposxml schema is supported while transitioning to the new repositem fields.
 */
public class SupportLegacySchema {

	private static final Logger logger = LoggerFactory.getLogger(SupportLegacySchema.class);
	
	/**
	 * Until {@link IndexingDoc#deepCopy()} can get only the {@link ItemPathinfo} and {@link ItemProperties} fields we use this to map repositem fields to reposxml schema.
	 * Key is field name, value is rename or null for using same name (we should end up with only nulls here).
	 */
	public static final Map<String, String> FIELDS_KEEP = new HashMap<String, String>() {private static final long serialVersionUID = 1L;{
		put("id", null);
		put("path", null);
		put("pathname", null);
		put("pathdir", null);
		put("pathin", null);
		put("pathext", null);
		put("pathfull", null);
		put("rev", null);
		put("revt", null);
		put("repo", null);
		put("repoparent", null);
		put("repohost", null);
	}};
	
//	// can these be the common ID field names?
//	CmsItemPath path = context.getItemPath();
//	RepoRevision rev = context.getRevision();
//	CmsRepository repo = context.getRepository();		
//	fields.addField("path", path.getPath());
//	fields.addField("pathname", path.getName());
//	CmsItemPath parent = path.getParent();
//	fields.addField("pathdir", parent == null ? "" : parent.getPath());
//	while (parent != null) {
//		fields.addField("pathin", parent.getPath());
//		parent = parent.getParent();
//	}
//	fields.addField("pathext", path.getExtension());
//	fields.addField("pathfull", repo.getPath() + path.getPath());
//	fields.addField("rev", rev.getNumber());
//	fields.addField("revt", getDateValue(rev.getDate()));
//	fields.addField("repo", repo.getName());
//	fields.addField("repoparent", repo.getParentPath());
//	if (repo.isHostKnown()) {
//		fields.addField("repohost", repo.getHost());
//	}	
	
	public void handle(IndexingDoc itemDoc) {
		Set<String> keep = FIELDS_KEEP.keySet();
		Set<String> remove = new LinkedHashSet<String>();
		for (String name : itemDoc.getFieldNames()) {
			if (name.startsWith("prop_")) continue;
			if (!keep.contains(name)) {
				logger.debug("Removing field '{}' not in xml keep list", name);
				remove.add(name);
			}
		}
		for (String r : remove) {
			itemDoc.removeField(r);
		}
	}

}
