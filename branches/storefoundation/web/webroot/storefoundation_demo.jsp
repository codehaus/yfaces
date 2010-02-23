<%@ page import="de.hybris.platform.catalog.jalo.Catalog" %>
<%@ page import="de.hybris.platform.catalog.jalo.CatalogManager" %>
<%@ page import="de.hybris.platform.catalog.jalo.CatalogVersion" %>
<%@ page import="de.hybris.platform.catalog.jalo.PreviewTicket" %>
<%@ page import="de.hybris.platform.util.WebSessionFunctions"%>
<%@ page import="de.hybris.platform.jalo.*"%>
<%@ page import="de.hybris.platform.jalo.user.*"%>
<%@ page import="de.hybris.platform.cms.jalo.CmsManager"%>
<%@ page import="de.hybris.platform.cms.jalo.Website"%>
<!DOCTYPE html PUBLIC "-//thestyleworks.de//DTD XHTML 1.0 Custom//EN" "../dtd/xhtml1-custom.dtd">

<%@page import="de.hybris.platform.core.Tenant"%>
<%@page import="de.hybris.platform.core.Registry"%>
<%@page import="de.hybris.platform.core.SlaveTenant"%><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<link rel="stylesheet" href="css/entryweb/hybris_main.css">
		<title>[<%=Registry.getCurrentTenant().getTenantID()%>] - StoreFoundation - hybris e-business software</title>
	
	</head>
	
	<%
		String redirect = request.getParameter("redirect");
		String clear = request.getParameter("clear") != null ? request.getParameter("clear").toLowerCase().trim() : null;
		String preview = request.getParameter("preview") != null ? request.getParameter("preview").toLowerCase().trim() : null;
		String versionID = request.getParameter("versionID") != null ? request.getParameter("versionID").trim() : "Staged";
		String liveedit = request.getParameter("liveedit") != null ? request.getParameter("liveedit").trim() : null;
	
		if (clear == null || clear.equals("true"))
		{
			session.invalidate();
	
			if (clear != null)
			{
				WebSessionFunctions.getSession(request);
			}
		}
	
		if (redirect != null)
		{
			response.sendRedirect(response.encodeURL("/storefoundation/" + redirect));
		}
	
		if (preview != null)
		{
			Catalog catalog = CatalogManager.getInstance().getCatalog(preview);
			CatalogVersion version = catalog.getCatalogVersion( versionID );
			PreviewTicket ticket = CatalogManager.getInstance().createPreviewTicket(version);
			String urlTemplate = catalog.getPreviewURLTemplate();
			response.sendRedirect( response.encodeRedirectURL(  urlTemplate + "?previewTicket=" + ticket.getTicketCode() ));
		}
		if( liveedit != null )
		{
			//Website site = CmsManager.getInstance().getWebsite( layout );
			//response.sendRedirect( response.encodeRedirectURL( "http://" + request.getServerName() + ":" + request.getServerPort() + "/hmc/hybris?open="+site.getPK()+"&user=admin&password=nimda") );
			User admin = UserManager.getInstance().getAdminEmployee();
			String token = UserManager.getInstance().generateLoginTokenCookieValue(admin.getUID(), "en", admin.getPassword());
			
			response.sendRedirect( response.encodeRedirectURL( "/hmc/hybris?t="+token+"&activateCMSTreeLeafChip=true") );
			
		}
	
	%>
	
	
	<body>
		<div id="head">
			&nbsp;
		</div>
		
		<div id="rightmargin">&nbsp;</div>
		<div id="headsystem" class="header">
<%
		Tenant hs = Registry.getCurrentTenant();
		if( hs instanceof SlaveTenant )
		{
%> 
		&lt;&lt;<%=hs.getTenantID()%>&gt;&gt; <br/>
		<a href="/?setmaster=true" style="color:white;font-size:10px;"> [Back to master tenant] </a>
<%
		}
%>
		</div>
		<div id="headtop">
			<img name="head_E-Business_Software" src="css/entryweb/HEAD_e-business_platform.gif"/>
			<br />
			<img src="css/entryweb/transp.gif" height="20"/>
			<br />
		
			<div class="header">hybris StoreFoundation Demonstration Page</div>
		</div>
		
		<div id="main">
		
			<div id="content">
		
				<!-- Headline START ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||-->
				<div class="absatz">
					<br />
					<table>
						<tr height="180" valign="top">
							<td width="45%">
								<h1>StoreFoundation</h1>
								<b>Note</b>: The following links work only correct if you have the 'sampledata' extension from our 'ext-sample' package installed.<p>
								<ul>
									<li>
									
										<% String _url1 = response.encodeURL("/storefoundation/storefoundation_demo.jsp?clear=true&redirect=index.jsf");%>
										<a target="_blank"	href="<%=_url1%>"> Demo Store Hardware and Clothes Catalog</a>
										<br/>
										<a target="_blank"	href="<%=_url1%>"><img src="images/blue.png"></a>
									</li>
									<li>
										<% String _url2 = response.encodeURL("/storefoundation/storefoundation_demo.jsp?clear=true&redirect=index.jsf?clothes");%>
										<a target="_blank" href="<%=_url2%>">Demo Store Clothes Catalog</a>
										<br/>
										<a target="_blank" href="<%=_url2%>"><img src="images/green.png"></a>
										<p/>
									</li>
									<li>
										<% String _url3 = response.encodeURL("/storefoundation/storefoundation_demo.jsp?clear=true&redirect=index.jsf?hardware");%>
										<a target="_blank" href="<%=_url3%>">Demo Store Hardware Catalog</a>
										<br/>
										<a target="_blank" href="<%=_url3%>"><img src="images/blue.png"></a>
										<p/><br/>
									</li>
									<li>

										<a target="_blank" href="<%=response.encodeURL("/storefoundation/storefoundation_demo.jsp?preview=clothescatalog&clear=true")%>">Preview <b>Staged</b> Version of Clothes catalog</a>
									</li>
									<li>
										<a target="_blank" href="<%=response.encodeURL("/storefoundation/storefoundation_demo.jsp?preview=hwcatalog&clear=true")%>">Preview <b>Staged</b> Version of Hardware catalog</a>
										<p />
									</li>
									<!-- 									
									<p>
									<li>
										<a href="/storefoundation/storefoundation_demo.jsp?layout=weblayout1&clear=true">Open the default StoreFoundation in the hybris Management Console</a>
									</li>
									<li>
										<a href="/storefoundation/storefoundation_demo.jsp?layout=weblayout2&clear=true">Open the StoreFoundation's alternative layout in the hybris Management Console</a>
									</li>
									-->
								</ul>
							</td>
							<td width="50">
								<!-- blank table column for better layout -->
							</td>
							<td>
								<h1>hybris Management Console</h1>
								<ul>
									<li>
										<a href="<%=response.encodeURL("/hmc/hybris")%>">hybris Management Console</a>
									</li>
									<br/>

									To open the StoreFoundation in <i>LiveEdit</i> mode, just
									open the "Web Sites" node and click on "LiveEdit" as you can
									see in the screenshot below.<br/> 
									<a href="<%=response.encodeURL("/hmc/hybris")%>"><img src="images/liveedit.png"/></a>
								</ul>
							</td>
						</tr>
						<tr valign="top">
							<td>
								<h1>ImpEx</h1>
								Using the ImpEx extension, you may create platform contents via CSV files.</br>
								<p />
								<ul>
									<li>
										<a href="<%=response.encodeURL("/impex")%>">ImpEx Demonstration page</a><br />
										You will find a number of sample CSV files in the <br />
										resources directory of the sampledata extension.<br />
										<p />
									</li>
									<li>
										<a href="http://dev.hybris.de/x/W4JvAg">ImpEx Documentation</a>
									</li>
								</ul>
							</td>
							<td>
								<!-- blank table column for better layout -->
							</td>
							<td width="45%">
								<h1>Administration pages</h1>
								<ul>
									<li>
										<a href="<%=response.encodeURL("/")%>">Administration page</a><br />
										If you want to initialize or update your system, follow this link.
									</li>
								</ul>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</body>
</html>
