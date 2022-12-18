package test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;




public class EndtoEnd {

	JsonPath jpath;
	Response response;
	Map<String, Object> MapObj;
	String URI = "http://localhost:3000";
	int Empid;

	@Test
	public void test1() {
		
		// Get all the Employees from the Json Server
		System.out.println(" Get all the Employees");
		response = GetAllEmployees();
		Assert.assertEquals(200, response.getStatusCode());

		// Create a new employee
		System.out.println("Creating a new employee");
		response = CreateNewEmployee("Jacob", "8000");
		System.out.println(response.getBody().asString());
		jpath = response.jsonPath();
		Empid = jpath.get("id");		
		Assert.assertEquals(201, response.statusCode());
		
		// Checking if the new employee is added
		System.out.println("checking if a new employee is added");
		response = GetSingleEmployee(Empid);
		System.out.println("The name of new employee is"+response.getBody().asString());
		JsonPath jpath1 =response.jsonPath();
		String name = jpath1.get("name");
		Assert.assertEquals(name,"Jacob");
		Assert.assertEquals(200, response.getStatusCode());

		// Updating the new employee name
		System.out.println("Updating the name of the new employee from Jacob to Smith");
		response = UpdateNewEmployee1(Empid,"Smith","8000");
		System.out.println("The updated name of new employee is"+response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());


		// Checking if the new name is updated
		System.out.println("checking if the new name update is reflecting");
		response = GetSingleEmployee(Empid);
		JsonPath jpath2 = response.jsonPath();
		String name1 = jpath2.get("name");
		Assert.assertEquals(name1, "Smith");
		Assert.assertEquals(200, response.getStatusCode());


		// Deleting the newly created employee
		System.out.println("Deleting the newly created employee");
		response = DeleteNewEmployee(Empid);
		Assert.assertEquals(200, response.statusCode());

		// Checking if the new employee is deleted
		System.out.println("checking if the new employee is deleted ");
		response = GetSingleEmployee(Empid);
		Assert.assertEquals(404, response.getStatusCode());

		// Checking again the list of employees existing currently
		System.out.println("Fetching all the employees");
		response = GetAllEmployees();
		JsonPath jpath3 = response.jsonPath();
		List<String> Ids = jpath3.get("id");
		Assert.assertFalse(Ids.contains(String.valueOf(Empid)));

	}	

	public Response GetAllEmployees() { 
		// This method will give all the employees detail

		RestAssured.baseURI = this.URI;

		RequestSpecification request = RestAssured.given();
		response = request.get("employees");
		System.out.println(response.getBody().asString());
		return response;

	}

	public Response GetSingleEmployee(int Empid) { 
		// This method will give single employee detail

		RestAssured.baseURI = this.URI;

		RequestSpecification request = RestAssured.given();
		Response response = request.get("employees/"+Empid);
		return response;

	}

	public Response CreateNewEmployee(String name, String salary) { 
		// This method will create a new employee

		RestAssured.baseURI = this.URI;
		RequestSpecification request = RestAssured.given();
		MapObj = new HashMap<String, Object>();

		MapObj.put("name", name);
		MapObj.put("salary", salary);		
		
		Response response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj)
				.post("employees/create");		
		return response;
	}

	public Response UpdateNewEmployee1(int Empid,String NewName,String salary) { 
		// This method will update the new employee

		RestAssured.baseURI = this.URI;
		RequestSpecification request = RestAssured.given();
		MapObj = new HashMap<String, Object>();

		MapObj.put("id", Empid);
		MapObj.put("name", NewName);
		MapObj.put("salary", salary);		

		Response response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj)
				.put("employees/"+Empid);		
		return response;

	}

	public Response DeleteNewEmployee(int Empid) { 
		// This method will delete the new employee
		RestAssured.baseURI = this.URI;
		RequestSpecification request = RestAssured.given();
		Response response = request.delete("employees/"+Empid);
		return response;
	}

}