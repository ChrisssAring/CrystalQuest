package nl.SugCube.CrystalQuest;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker {
	
	public static CrystalQuest plugin;
	private URL files;
	
	public UpdateChecker(CrystalQuest instance, String url) {
		plugin = instance;
		
		try {
			this.files = new URL(url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean updateAvaiable() {
		try {
			InputStream is = this.files.openConnection().getInputStream();
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			
			Node latest = document.getElementsByTagName("item").item(0);
			NodeList children = latest.getChildNodes();
			String version = children.item(1).getTextContent();
			String currentVersion = plugin.getDescription().getVersion();
			
			if (!version.replace("Version ", "").equalsIgnoreCase(currentVersion)) {
				return true;
			}
		} catch (Exception ex) { }
		
		return false;
	}

}
