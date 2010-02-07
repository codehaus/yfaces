package de.hybris.yfaces.demo;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.str.prettysource.InputNode;
import de.str.prettysource.output.HtmlOutputFormat;
import de.str.prettysource.output.HtmlOutputNode;
import de.str.prettysource.output.HtmlOutputNodeFactory;

public class Xhtml2HtmlFormatFactory {

	public static HtmlOutputFormat getXhtmlToHtmlFormat() {
		InputNode pTree = XhtmlInputDef.createParserTree("XHTML");

		HtmlOutputFormat result = new HtmlOutputFormat(pTree);
		registerHtmlFormatEnums(result, XhtmlToHtmlOutputNodes.values());
		result.setOutputNode("XHTML", new HtmlOutputNode());

		return result;
	}

	private static void registerHtmlFormatEnums(HtmlOutputFormat htmlFormat,
			HtmlOutputNodeFactory[] values) {
		for (HtmlOutputNodeFactory value : values) {
			htmlFormat.setOutputNode(value.getNodeId(), value.createNode());
			htmlFormat.registerStyleClass(value.getStyleClass(), value.getStyleValue());
		}
	}

	//
	//
	// Testing area
	//
	/**
	 * Javadoc
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		// test();
		String sourceName = "";
		String targetName = "";

		if (argc.length > 1) {

		} else {
			String base = "D:/projects/yfaces/yfaces-demo/src/main/webapp";
			sourceName = base + "/demo/basic/demoCmp.xhtml";
			// sourceName = base + "/demo/template.xhtml";
			// sourceName = base + "/demo/binding1/demoCmp.xhtml";
			targetName = "C:/_test/";
		}

		File sourceFile = new File(sourceName);
		File targetFile = new File(targetName);

		// for (int i = 0; i <= 10; i++) {
		long t1 = System.currentTimeMillis();
		HtmlOutputFormat source = Xhtml2HtmlFormatFactory.getXhtmlToHtmlFormat();
		source.format(sourceFile, targetFile);
		long t2 = System.currentTimeMillis();
		System.out.println("Took: " + (t2 - t1) + "ms");
		// }
	}

	public static void test() {
		Pattern p = Pattern.compile("(\")|(\\w+(?=\\s*=))|(>)");
		Matcher m = p.matcher("attribute=\"test\" ");
		if (m.find())
			System.out.println(m.group());
		System.exit(0);
	}
}
