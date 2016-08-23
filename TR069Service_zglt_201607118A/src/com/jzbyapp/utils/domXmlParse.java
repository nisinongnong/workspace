package com.jzbyapp.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.provider.Settings;


/**
 * xml文件解析工具，采用dom解析方式.xml文件由固定格式，文件只要遵守格式可以灵活修改和追加.
 * 该xml文件的解析主要是为程序初始化使用，因为终端设备对接前端需要初始化前端指定的节点值才能通过认证上线.
 * 使用xml文件的方式初始化可以规避生产和升级两种情况下第一次开机初始化数据的矛盾性，对后续版本的维护性也能
 * 提供便利性，详细信息见相关文档.
 * @author
 */
public class domXmlParse {
	private int versionRecord = 0;
	
	public void parse(Context context, int versionId, InputStream is){
		LogUtils.i("domXmlParse parse() is in >>> " + versionId);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			Element rootElement = doc.getDocumentElement();
			LogUtils.i("the root node name is >>> " + rootElement.getNodeName());
			if("Nodes".equals(rootElement.getNodeName())){
				NodeList nodelist = rootElement.getChildNodes();
				for(int i=0; i<nodelist.getLength(); i++){
					Node versions = nodelist.item(i);
					if (versions.getNodeType() == Node.ELEMENT_NODE) {
						LogUtils.i("the node is >>> " + versions.getNodeName() + "_" + i);
						if ("VERSIONCODE".equals(versions.getNodeName())) {
							versionRecord = Integer.parseInt(versions.getAttributes().getNamedItem("version").getNodeValue());
							LogUtils.i("VERSIONCODE is >>> "+ versionRecord);
							NodeList nodes = versions.getChildNodes();
							//Log.i("lrui", "the length of nodes is " + nodes.getLength());
							for (int j=0; j < nodes.getLength(); j++) {
								Node node = nodes.item(j);
								if((node.getNodeType() == Node.ELEMENT_NODE) && (versionId < versionRecord)){
										//Log.i("lrui","the node name is " + node.getNodeName());
										if ("KeyData".equals(node.getNodeName())) {
											NodeList keydata = node.getChildNodes();
												for (int k=0; k < keydata.getLength(); k++) {
													Node keynodes = keydata.item(k);
													if(keynodes.getNodeType() == Node.ELEMENT_NODE){
														/*Log.i("lrui", "the keynode name is "+ keynodes.getNodeName()
																+ "  and the key is "+ keynodes.getAttributes().getNamedItem("key").getNodeValue()
																+ "  the value is "+ keynodes.getAttributes().getNamedItem("value").getNodeValue());*/
														String key = keynodes.getAttributes().getNamedItem("key").getNodeValue();
														String value = keynodes.getAttributes().getNamedItem("value").getNodeValue();
														//Settings.System.putString(context.getContentResolver(), key, value);
														Config.mDevInfoManager.update(key, value, Config.devVersion);
													}	
												}
										}else if("Node".equals(node.getNodeName())){
											/*Log.i("lrui", "the nodes name is " + nodes.item(j).getNodeName()
													+ "  and the key is "+nodes.item(j).getAttributes().getNamedItem("key").getNodeValue()
													+ "  the value is "+nodes.item(j).getAttributes().getNamedItem("value").getNodeValue());*/
											String key = nodes.item(j).getAttributes().getNamedItem("key").getNodeValue();
											String value = nodes.item(j).getAttributes().getNamedItem("value").getNodeValue();
											Settings.System.putString(context.getContentResolver(), key, value);
										}
								}								
							}
						}

					}
				}
			}
			Settings.System.putInt(context.getContentResolver(),"JiuZhou.STB.VersionControl",versionRecord);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getVersionSwitch(InputStream is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			Element rootElement = doc.getDocumentElement();
			LogUtils.i("the root node name is >>> " + rootElement.getNodeName());

			if("Nodes".equals(rootElement.getNodeName())){
				NodeList nodelist = rootElement.getChildNodes();
				for(int i=0; i<nodelist.getLength(); i++){
					Node versions = nodelist.item(i);
					if (versions.getNodeType() == Node.ELEMENT_NODE) {
						LogUtils.i("the node is >>> " + versions.getNodeName() + "_" + i);
						if ("VERSIONCODE".equals(versions.getNodeName())) {
							versionRecord = Integer.parseInt(versions.getAttributes().getNamedItem("version").getNodeValue());
							LogUtils.i("VERSIONCODE is >>> "+ versionRecord);
							NodeList nodes = versions.getChildNodes();
							for (int j=0; j < nodes.getLength(); j++) {
								Node node = nodes.item(j);
								if(node.getNodeType() == Node.ELEMENT_NODE) {
									if ("KeyData".equals(node.getNodeName())) {
										NodeList keydata = node.getChildNodes();
										for (int k=0; k < keydata.getLength(); k++) {
											Node keynodes = keydata.item(k);
											if(keynodes.getNodeType() == Node.ELEMENT_NODE){
												String key = keynodes.getAttributes().getNamedItem("key").getNodeValue();
												if(key.equals(Config.VERSIONSWITCH)) {
													String value = keynodes.getAttributes().getNamedItem("value").getNodeValue();
													return value;
												}
											}	
										}																
									}
								}								
							}
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Config.StandarVersion;
	}
}
