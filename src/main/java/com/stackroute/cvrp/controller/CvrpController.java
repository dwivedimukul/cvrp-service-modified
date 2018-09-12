package com.stackroute.cvrp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.cvrp.domain.Route;
import com.stackroute.cvrp.service.RoutingService;

@RestController
@RequestMapping("api/v1/cvrp")
public class CvrpController {

	private RoutingService routingService;
	@Autowired
	public CvrpController(RoutingService routingService) {
		this.routingService=routingService;
	}
	
	
	@RequestMapping(value = "/slots", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> giveSlots(@RequestBody Route route){
		Route newRoute;
		System.out.println("controller-->");
		newRoute = routingService.getOrderedRoute(route);
		return new ResponseEntity<Route>(newRoute,HttpStatus.OK);
	}	

}
