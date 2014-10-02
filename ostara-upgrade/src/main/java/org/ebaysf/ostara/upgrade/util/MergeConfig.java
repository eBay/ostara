/*******************************************************************************
 * Copyright (c) 2014 eBay Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.ebaysf.ostara.upgrade.util;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class MergeConfig {
	
	private static final String CONFIG_FILE = "mergeconfig.xml";
	
	private static final MergeConfig instance = new MergeConfig();
	
	private String defaultVersion;
	
	private String defaultMergedBndProperties;
	
	private Collection<MergeData> mergeDataSet = new HashSet<MergeData>();
	

	static {

		parseXml();
	
	}
	
	
	private MergeConfig()
	{
		
	}
	
	
	public static MergeConfig getInstance()
	{
		return instance;
		
	}
	
	
	
	
	public static void main(String[] args) 
	{
		
		parseXml();
		
	}
	
	
	private static void parseXml()
	{
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		MergeConfig config = MergeConfig.getInstance();
		
		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputStream	is = MergeConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
			Document dom = db.parse(is);
			
			Element docEle = dom.getDocumentElement();

			
			
			NodeList nl1 = docEle.getElementsByTagName("defaultversion");
			String defaultversion = nl1.item(0).getTextContent();
			config.setDefaultVersion(defaultversion);
			
			NodeList nl0 = docEle.getElementsByTagName("defaultbnd");
			String defaultBnd = nl0.item(0).getTextContent();
			config.setDefaultMergedBndProperties(defaultBnd);

			
			//get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("Merge");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {

					//get the employee element
					Element el = (Element)nl.item(i);
					
					
					MergeConfig.MergeData md =  config.new MergeData();
					
					String bsn = el.getAttribute("bsn");
					md.setBsn(bsn);
					String version = el.getAttribute("version");
					md.setVersion(version);
					String groupid = el.getAttribute("groupid");
					md.setGroupId(groupid);
					String bnd = el.getAttribute("bnd");
					md.setBnd(bnd);

					NodeList nl2 = el.getElementsByTagName("ComponentArtifact");
					
					for(int j=0 ; j<nl2.getLength() ; j++) {
						
						md.addComponentJar(nl2.item(j).getTextContent().trim());
					}
					config.addMergeData(md);
				}
			}
			
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		
	}
	

	
	public String getDefaultMergedBndProperties() {
		return defaultMergedBndProperties;
	}


	public void setDefaultMergedBndProperties(String defaultBndProperties) {
		this.defaultMergedBndProperties = defaultBndProperties;
	}


	public String getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(String defaultVersion) {
		this.defaultVersion = defaultVersion;
	}

	public Collection<MergeData> getMergeDataSet() {
		return mergeDataSet;
	}

	public void addMergeData (MergeData aMergeData) {
		this.mergeDataSet.add(aMergeData);
	}

	public MergeData getMergeData(String mergedFileName) {
		for (MergeData md : mergeDataSet) {
			if (md.getBsn().equals(mergedFileName)) {
				return md;
			}
		}
		return null;
	}
	
	public class MergeData
	{
		private String bsn;
		
		private String version;
		
		private String bnd;
		
		private String groupId;
		
		@Override
		public String toString() {return String.format("[%s:%s:%s:%s]", groupId, bsn, bnd, groupId);}
		
		private SortedSet<String> componentJarSet = new TreeSet<String>();

		public String getBsn() {
			return bsn;
		}

		public void setBsn(String bsn) {
			this.bsn = bsn;
		}

		public String getBnd() {

			if (bnd == null || "".equals(bnd.trim()))
			{
				return defaultMergedBndProperties;
			}
			else
			{
				return bnd;
			}
		}

		public void setBnd(String bnd) {
			this.bnd = bnd;
		}

		public String getVersion() {
			if (version == null || "".equals(version.trim()))
			{
				return defaultVersion;
			}
			else
			{
				return version;
			}
		}

		public void setVersion(String version) {
			this.version = version;
		}
		
		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public SortedSet<String> getComponentJarSet() {
			return componentJarSet;
		}

		public void addComponentJar(String aComponentJar) {
			
			componentJarSet.add(aComponentJar);
		}
		
	}	
	
	
	
}
