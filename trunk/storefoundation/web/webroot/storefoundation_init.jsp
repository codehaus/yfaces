<%@ page import="de.hybris.platform.util.WebSessionFunctions"%>
<%@ page import="de.hybris.platform.jalo.JaloSession"%>

<%@page import="de.hybris.platform.jalo.user.*"%>
<%@page import="de.hybris.platform.jalo.order.Order"%>
<%@page import="java.util.Collection"%>

<%@page import="de.hybris.platform.core.Registry"%><html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title> [<%=Registry.getCurrentTenant().getTenantID()%>] - Storefoundation - hybris e-business software </title>
	</head>
	
	<%	session.invalidate(); %>
	<% 
		if( JaloSession.hasCurrentSession() ) 
		{ 
			JaloSession.getCurrentSession().close(); JaloSession.deactivate(); 
		}
		
		if (request.getParameter("clear") != null)
		{
			try
			{
				Customer customer = UserManager.getInstance().getCustomerByLogin("test1");
				if (customer != null)
				{
					Collection<Order> orders = customer.getOrders();
					for (Order order : orders)
						order.remove();
					customer.remove();
				}
			}
			catch (Exception e)
			{
			}

			System.out.println( "Clear" );
		}
%>
	
	<body>
		<center>
		<h1>Selenium vs. Storefoundation</h1><br>
		This page is for testing purposes only and does nothing more than clean the Jalo session and set the language to DE.<br><br>
		Parameters:<br>
		<ul>
			<li><b>clear</b>: delete all Selenium generated stuff.</li>
	   </ul>
		<br/>
		<br/>
		<a href="<%=response.encodeURL("/storefoundation/index.jsf?lang=de")%>">Storefoundation</a>
		</center>
	</body>
</html>
