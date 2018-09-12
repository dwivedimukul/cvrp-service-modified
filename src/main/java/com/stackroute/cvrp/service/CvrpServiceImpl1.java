package com.stackroute.cvrp.service;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import com.stackroute.cvrp.domain.DateLogistics;
import com.stackroute.cvrp.domain.Location;
import com.stackroute.cvrp.domain.Order;
import com.stackroute.cvrp.domain.Route;
import com.stackroute.cvrp.domain.Slot;
import com.stackroute.cvrp.domain.Vehicle;
import com.stackroute.cvrp.exceptions.IllegalLocationMatrixException;

@Service
public class CvrpServiceImpl1 implements CvrpService {

	public CvrpServiceImpl1() {
		super();
		// TODO Auto-generated constructor stub
	}

	private Route list;
	private Slot[] slots;
	private Vehicle[] vehicles;
	private Vehicle[] vehiclesForBestSolution;
	private double bestSolutionCost;
	private ArrayList<Double> pastSolutions;
	private int noOfVehicles;
	private int noOfOrders;
	private double distance;
	private float filledSlotCapacity = 0;
	private float totalSlotCapacity = 0;
	private float newFilledCapacity;
	private float vehicleFilledCapacity;
	private float vehicleTotalCapacity;

	@Override
	public void setJson(Route route) {
		this.list = route;
	}
	
	@Override
	public Route getJson() {
		return this.list;
	}

	public CvrpServiceImpl1(int orderNum, int vehNum, int vehCap) {
		this.noOfVehicles = vehNum;
		this.noOfOrders = orderNum;
		this.distance = 0;
		vehicles = new Vehicle[noOfVehicles];
		vehiclesForBestSolution = new Vehicle[noOfVehicles];
		pastSolutions = new ArrayList<>();
		for (int i = 0; i < noOfVehicles; i++) {
			vehicles[i] = new Vehicle(i + 1, vehCap);
			System.out.println("vehicles"+vehicles[i]);
			vehiclesForBestSolution[i] = new Vehicle(i + 1, vehCap);
			vehicles[i].setVehicleRoute(null);
			for (int j = 0; j < vehicles.length; j++) {
				vehicles[i].getVehicleRoute()[j].setRouted(false);
				
			}
		}

	}

	public Order getNewOrder() {
		Order newOrder;
		newOrder = this.getJson().getNewOrder();
		return newOrder;
	}

	public Location getNewOrderLocation() {
		Location location;
		location = this.getNewOrder().getOrderLocation();
		return location;
	}

	public String getNewOrderVolume() {
		String newOrderVolume;
		newOrderVolume = this.getNewOrder().getOrderVolume();
		return newOrderVolume;
	}

	public DateLogistics getDateLogistics() {
		DateLogistics dateLogistics;
		dateLogistics = this.getJson().getDateLogistics();
		return dateLogistics;
	}

	public Slot[] getSlots() {
		Slot[] slots;
		slots = this.getDateLogistics().getSlots();
		return slots;
	}

	@Override
	public List<Location> getAllLocationsBySlot(String slotId) {
		// Slot[] slots = this.getSlots();
		// Vehicle[] vehicles;
		// String id;
		// List<Order> orders = new ArrayList<>();
		// Location location;
		// List<Location> locations = new ArrayList<>();
		// Location newOrderLocation = this.getNewOrderLocation();
		//
		// for (int i = 0; i < slots.length; i++) {
		// id = slots[i].getSlotId();
		// if (slotId == id) {
		// vehicles = slots[i].getSlotVehicle();
		// for (int j = 0; j < vehicles.length; j++) {
		// vehicles[j].setVehicleCurrentLocation("0");
		// if (vehicles[j].getVehicleRoute() == null) {
		//
		// } else {
		// orders = Arrays.asList(vehicles[j].getVehicleRoute());
		// for (int k = 0; k < orders.size(); k++) {
		// location = orders.get(k).getOrderLocation();
		// locations.add(location);
		// }
		// }
		// }
		//
		// }
		// }
		// locations.add(newOrderLocation);
		// return locations;
		//
		List<Order> order = new ArrayList<Order>();
		List<Location> locations = new ArrayList<Location>();
		order = this.getAllOrders(slotId);
		for (int i = 0; i < order.size(); i++) {
			locations.add(order.get(i).getOrderLocation());
		}
		return locations;
	}

	public List<Order> getAllOrders(String slotId) {

		Slot[] slots = this.getSlots();
		Vehicle[] slotVehicles;
		List<Order> orders = new ArrayList<Order>();
		List<Order> ordersResult = new ArrayList<Order>();
		Order[] vehicleRoute;
		Order single;
		String id;
		Order[] orderArray;

		for (int i = 0; i < slots.length; i++) {
			id = slots[i].getSlotId();
			if (slotId.equals(id)) {
				slotVehicles = slots[i].getSlotVehicle();

				for (int j = 0; j < slotVehicles.length; j++) {
					System.out.println(slotVehicles[j] + "vehicles");
					vehicleRoute = slotVehicles[j].getVehicleRoute();

					if (slotVehicles[j].getVehicleRoute() != null) {
						// orders=Collections.<Order>emptyList();
						// orders=null;
						for (int k = 0; k < vehicleRoute.length; k++) {
							single = vehicleRoute[k];
							single.setRouted(false);
							// orders.get(i).setRouted(false);
							orders.add(single);
							// orders = new ArrayList<Order>(Arrays.asList(vehicles[j].getVehicleRoute()));
							// orders=vehicles[j].getVehicleRoute();

						}
					}
				}

			}
			// int n=orders.length;
			System.out.println("new order" + this.getNewOrder());
			// System.out.println("orderssdlfkf "+o);
			orders.add(this.getNewOrder());
			// orders[n+1]=this.getNewOrder();

		}

		return orders;
	}

	@Override
	public double[][] getDistanceMatrix(String slotId) {
		String url1 = "https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?";
		String origins = "origins=";
		String origin = "";
		int count = 0;
		String destinations = "destinations=";
		String destination = "";
		String url2 = "travelMode=driving&key=AhT3nVgSlv14w5u2GLYkCrCJm1VWDkBeEGHpG4JFNb13vgktN7OIJEr-5KZZrZah";
		String inline = "";
		List<Location> locations;
		locations = getAllLocationsBySlot(slotId);
		double[][] distanceMatrix = new double[locations.size()][locations.size()];
		while (!(locations.isEmpty())) {

			if (count < 1) {
				for (int i = 0; i < locations.size(); i++) {
					for (int j = 0; j < 1; j++) {
						String str1 = locations.get(i).getOrderLatitude();
						String str2 = locations.get(i).getOrderLongitude();
						if (i < locations.size() - 1) {
							origins = origins + str1 + "," + str2;
							destinations = destinations + str1 + "," + str2;
						} else {
							origins = origins + str1 + "," + str2 + ";";
							destinations = destinations + str1 + "," + str2 + ";";

						}
					}

				}
				origin = origins.substring(0, origins.length() - 1);
				destination = destinations.substring(0, destinations.length() - 1);
				String url = url1 + origin + "&" + destination + "&" + url2;
				System.out.println("Url is " + url);

				try {
					count++;
					URL url3 = new URL(
							"https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?origins=12.9623455,77.6373747;12.9353915,77.611746;13.0107953,77.5526921;12.9647254,77.719147&destinations=12.9623455,77.6373747;12.9353915,77.611746;13.0107953,77.5526921;12.9647254,77.719147&travelMode=driving&key=AhT3nVgSlv14w5u2GLYkCrCJm1VWDkBeEGHpG4JFNb13vgktN7OIJEr-5KZZrZah");

					// Double[][] distanceMatrix = new Double[locations.size()][locations.size()];
					HttpURLConnection conn = (HttpURLConnection) url3.openConnection();
					conn.setRequestMethod("GET");
					conn.connect();
					int responsecode = conn.getResponseCode();
					if (responsecode != 200)
						throw new IllegalLocationMatrixException("HttpResponseCode: " + responsecode);
					else {
						Scanner sc = new Scanner(url3.openStream());
						while (sc.hasNext()) {
							inline += sc.nextLine();
						}
						System.out.println("\nJSON Response in String format");
						System.out.println(inline);
						sc.close();
					}

					JSONParser parse = new JSONParser();
					JSONObject jobj = (JSONObject) parse.parse(inline);
					JSONArray jsonarr_1 = (JSONArray) jobj.get("resourceSets");
					JSONObject jsonobj_1 = (JSONObject) jsonarr_1.get(0);
					JSONArray jsonarr_2 = (JSONArray) jsonobj_1.get("resources");
					JSONObject jsonobj_3 = (JSONObject) jsonarr_2.get(0);
					JSONArray jsonarr_3 = (JSONArray) jsonobj_3.get("results");
					for (int j = 1; j < jsonarr_3.size(); j++) {
						JSONObject jsonobj_2 = (JSONObject) jsonarr_3.get(j);
						int str_data1 = ((Long) jsonobj_2.get("destinationIndex")).intValue();
						System.out.println(str_data1);
						int str_data2 = ((Long) jsonobj_2.get("originIndex")).intValue();
						System.out.println(str_data2);
						// Long str_data3 = (Long) jsonobj_2.get("totalWalkDuration");
						// System.out.println(str_data3);
						try {
							Double str_data4 = (Double) jsonobj_2.get("travelDistance");
							System.out.println(str_data4);
							// Double str_data5 = (Double) jsonobj_2.get("travelDuration");
							// System.out.println(str_data5);
							if (str_data1 != str_data2) {
								distanceMatrix[str_data1][str_data2] = str_data4.doubleValue();
								distanceMatrix[str_data2][str_data1] = str_data4.doubleValue();
							} else {
								distanceMatrix[str_data1][str_data1] = 0;
							}
						} catch (Exception e) {
							// // Long str_data4 = (Long) jsonobj_2.get("travelDistance");
							// // //System.out.println(str_data4);
							// // Long str_data5 = (Long) jsonobj_2.get("travelDuration");
							// // //System.out.println(str_data5);
							//
						}
						System.out.println("\n");

					}
					// for(int i=0;i<distanceMatrix.length;i++) {
					// for(int j=0;j<distanceMatrix.length;j++)
					// System.out.println(i+" "+ j+" "+distanceMatrix[i][j]);
					// }

					conn.disconnect();
					return distanceMatrix;
				} catch (Exception e) {
					e.printStackTrace();

					// break;
				}
			}

		}
		for (int i = 0; i < distanceMatrix.length; i++) {
			for (int j = 0; j < distanceMatrix.length; j++)
				System.out.println("in distance matrix" + distanceMatrix[i][j]);
		}

		return distanceMatrix;
	}

	public boolean checkIfFits(String demand) {
		boolean result = false;
		demand = this.getNewOrderVolume();
		slots = this.getSlots();
		for (int i = 0; i < slots.length; i++) {
			vehicles = slots[i].getSlotVehicle();
			for (int j = 0; j < vehicles.length; j++) {
				totalSlotCapacity += Float.parseFloat(vehicles[j].getVehicleCapacity());
				if (vehicles[j].getVehicleLoadedCapacity() == null) {
					filledSlotCapacity = 0;
					vehicleFilledCapacity = 0;
				} else {
					filledSlotCapacity += Float.parseFloat(vehicles[j].getVehicleLoadedCapacity());
					vehicleFilledCapacity = Float.parseFloat(vehicles[j].getVehicleLoadedCapacity());
				}
				vehicleTotalCapacity = Float.parseFloat(vehicles[i].getVehicleCapacity());
			}
			newFilledCapacity = filledSlotCapacity + Float.parseFloat(demand);
			if (newFilledCapacity <= totalSlotCapacity) {
				if (vehicleFilledCapacity + Float.parseFloat(demand) <= vehicleTotalCapacity) {
					result = true;
				}
			}
		}
		return result;
	}

	public int getNoOfVehicles(String slotId) {
		Slot[] slots = this.getSlots();
		String numberOfVehicles;
		int numOfVehicles = 0;
		for (int i = 0; i < slots.length; i++) {
			if (slotId == slots[i].getSlotId()) {
				numberOfVehicles = slots[i].getSlotNoOfVehicle();
				numOfVehicles = Integer.parseInt(numberOfVehicles);
			}
		}
		return numOfVehicles;
	}

	public boolean UnassignedOrderExists(Order[] orders) {
		for (int i = 1; i < orders.length; i++) {
			if (!orders[i].isRouted())
				return true;
		}
		return false;
	}

	public void greedySolution(Order[] orders, double[][] distanceMatrix) {

		double candCost, endCost;
		int vehicleIndex = 0;
		// String slotId = null;
		// Vehicle[] vehicleArray = this.vehiclesArray;

		while (UnassignedOrderExists(orders)) {

			int orderIndex = 0;
			Order orderObj = null;
			double minCost = (float) Double.MAX_VALUE;
			if (vehicles[vehicleIndex].getVehicleRoute() == null) {
				vehicles[vehicleIndex].addOrder(orders[0]);
			}
			for (int i = 1; i <= noOfOrders; i++) {
				if (orders[i].isRouted() == false) {
					if (this.checkIfFits(orders[i].getOrderVolume())) {
						candCost = distanceMatrix[Integer
								.parseInt(vehicles[vehicleIndex].getVehicleCurrentLocation())][i];
						if (minCost > candCost) {
							minCost = candCost;
							orderIndex = i;
							orderObj = orders[i];
						}
					}
				}
			}
			if (orderObj == null) {
				if (vehicleIndex + 1 < vehicles.length) {
					if (Integer.parseInt(vehicles[vehicleIndex].getVehicleCurrentLocation()) != 0) {
						endCost = distanceMatrix[Integer
								.parseInt(vehicles[vehicleIndex].getVehicleCurrentLocation())][0];
						vehicles[vehicleIndex].addOrder(orders[0]);
						this.distance += endCost;
					}
					vehicleIndex = vehicleIndex + 1;
				} else {
					System.exit(0);
				}

			} else {
				vehicles[vehicleIndex].addOrder(orderObj);
				orders[orderIndex].setRouted(true);
				this.distance += minCost;
			}

		}
		endCost = distanceMatrix[Integer.parseInt(vehicles[vehicleIndex].getVehicleCurrentLocation())][0];
		vehicles[vehicleIndex].addOrder(orders[0]);
		this.distance += endCost;
	}

	public void TabuSearch(int TABU_Horizon, double[][] distanceMatrix) {
		System.out.println("inside tabu search");

		// We use 1-0 exchange move
		List<Order> RouteFrom = new ArrayList<>();
		List<Order> RouteTo = new ArrayList<>();

		String MovingNodeDemand = null;

		int VehIndexFrom, VehIndexTo;
		double BestNCost, NeigthboorCost;

		int SwapIndexA = -1, SwapIndexB = -1, SwapRouteFrom = -1, SwapRouteTo = -1;

		int MAX_ITERATIONS = 200;
		int iteration_number = 0;

		int DimensionCustomer = distanceMatrix[1].length;
		int TABU_Matrix[][] = new int[DimensionCustomer + 1][DimensionCustomer + 1];

		String slotId = null;
		bestSolutionCost = this.distance; // Initial Solution Cost
		// System.out.println("Best SOlution cost:"+BestSolutionCost);

		boolean Termination = false;

		while (!Termination) {
			iteration_number++;
			BestNCost = Double.MAX_VALUE;
			// System.out.println("Best cost"+BestNCost);

			for (VehIndexFrom = 0; VehIndexFrom < this.vehicles.length; VehIndexFrom++) {
				RouteFrom = Arrays.asList(this.vehicles[VehIndexFrom].getVehicleRoute());

				int RoutFromLength = RouteFrom.size();
				// System.out.println(RoutFromLength);
				for (int i = 1; i < RoutFromLength - 1; i++) { // Not possible to move depot!

					for (VehIndexTo = 0; VehIndexTo < this.vehicles.length; VehIndexTo++) {
						RouteTo = Arrays.asList(this.vehicles[VehIndexTo].getVehicleRoute());
						int RouteTolength = RouteTo.size();
						// System.out.println("hey"+RouteTolength);
						for (int j = 0; (j < RouteTolength - 1); j++) {// Not possible to move after last Depot!

							MovingNodeDemand = RouteFrom.get(i).getOrderVolume();

							if ((VehIndexFrom == VehIndexTo) || this.checkIfFits(MovingNodeDemand)) {
								// If we assign to a different route check capacity constrains
								// if in the new route is the same no need to check for capacity

								if (((VehIndexFrom == VehIndexTo) && ((j == i) || (j == i - 1))) == false) // Not a
																											// move
																											// that
																											// Changes
																											// solution
																											// cost
								{ // System.out.println("i "+i);
									// System.out.println("j "+j);
									double MinusCost1 = distanceMatrix[Integer
											.parseInt(RouteFrom.get(i - 1).getOrderId())][Integer
													.parseInt(RouteFrom.get(i).getOrderId())];
									// System.out.println("Minus Cost 1 " + i + " " + MinusCost1);
									double MinusCost2 = distanceMatrix[Integer
											.parseInt(RouteFrom.get(i).getOrderId())][Integer
													.parseInt(RouteFrom.get(i + 1).getOrderId())];
									// System.out.println("Minus Cost 2 " + i + " " + MinusCost2);
									double MinusCost3 = distanceMatrix[Integer
											.parseInt(RouteTo.get(j).getOrderId())][Integer
													.parseInt(RouteTo.get(j + 1).getOrderId())];
									// System.out.println("Minus Cost 3 " + i + " " + MinusCost3);

									double AddedCost1 = distanceMatrix[Integer
											.parseInt(RouteFrom.get(i - 1).getOrderId())][Integer
													.parseInt(RouteFrom.get(i + 1).getOrderId())];
									// System.out.println("Added Cost 1 " + i + " " + " " + j + AddedCost1);
									double AddedCost2 = distanceMatrix[Integer
											.parseInt(RouteTo.get(j).getOrderId())][Integer
													.parseInt(RouteFrom.get(i).getOrderId())];
									// System.out.println("Added Cost 2 " + i + " " + " " + j + AddedCost2);
									double AddedCost3 = distanceMatrix[Integer
											.parseInt(RouteFrom.get(i).getOrderId())][Integer
													.parseInt(RouteTo.get(j + 1).getOrderId())];
									System.out.println("Added Cost 3 " + i + " " + " " + j + AddedCost3);

									// Check if the move is a Tabu! - If it is Tabu break
									if ((TABU_Matrix[Integer.parseInt(RouteFrom.get(i - 1).getOrderId())][Integer
											.parseInt(RouteFrom.get(i + 1).getOrderId())] != 0)
											|| (TABU_Matrix[Integer.parseInt(RouteTo.get(j).getOrderId())][Integer
													.parseInt(RouteFrom.get(i).getOrderId())] != 0)
											|| (TABU_Matrix[Integer.parseInt(RouteFrom.get(i).getOrderId())][Integer
													.parseInt(RouteTo.get(j + 1).getOrderId())] != 0)) {
										break;
									}

									NeigthboorCost = AddedCost1 + AddedCost2 + AddedCost3 - MinusCost1 - MinusCost2
											- MinusCost3;
									// System.out.println(NeigthboorCost);

									if (NeigthboorCost < BestNCost) {
										BestNCost = NeigthboorCost;
										SwapIndexA = i;
										SwapIndexB = j;
										SwapRouteFrom = VehIndexFrom;
										SwapRouteTo = VehIndexTo;
									}
								}
							}
						}
					}
				}
			}

			for (int o = 0; o < TABU_Matrix[0].length; o++) {
				for (int p = 0; p < TABU_Matrix[0].length; p++) {
					if (TABU_Matrix[o][p] > 0) {
						TABU_Matrix[o][p]--;
					}
				}
			}

			RouteFrom = Arrays.asList(this.vehicles[SwapRouteFrom].getVehicleRoute());
			RouteTo = Arrays.asList(this.vehicles[SwapRouteTo].getVehicleRoute());
			this.vehicles[SwapRouteFrom].setVehicleRoute(null);
			this.vehicles[SwapRouteTo].setVehicleRoute(null);

			Order SwapNode = RouteFrom.get(SwapIndexA);

			int NodeIDBefore = Integer.parseInt(RouteFrom.get(SwapIndexA - 1).getOrderId());
			int NodeIDAfter = Integer.parseInt(RouteFrom.get(SwapIndexA + 1).getOrderId());
			int NodeID_F = Integer.parseInt(RouteTo.get(SwapIndexB).getOrderId());
			int NodeID_G = Integer.parseInt(RouteTo.get(SwapIndexB + 1).getOrderId());

			Random TabuRan = new Random();
			int RendomDelay1 = TabuRan.nextInt(5);
			int RendomDelay2 = TabuRan.nextInt(5);
			int RendomDelay3 = TabuRan.nextInt(5);

			TABU_Matrix[NodeIDBefore][Integer.parseInt(SwapNode.getOrderId())] = TABU_Horizon + RendomDelay1;
			TABU_Matrix[Integer.parseInt(SwapNode.getOrderId())][NodeIDAfter] = TABU_Horizon + RendomDelay2;
			TABU_Matrix[NodeID_F][NodeID_G] = TABU_Horizon + RendomDelay3;

			RouteFrom.remove(SwapIndexA);

			if (SwapRouteFrom == SwapRouteTo) {
				if (SwapIndexA < SwapIndexB) {
					RouteTo.add(SwapIndexB, SwapNode);
				} else {
					RouteTo.add(SwapIndexB + 1, SwapNode);
				}
			} else {
				RouteTo.add(SwapIndexB + 1, SwapNode);
			}

			this.vehicles[SwapRouteFrom].setVehicleRoute(RouteFrom.toArray(new Order[RouteFrom.size()]));

			this.vehicles[SwapRouteFrom].setVehicleLoadedCapacity(
					Integer.toString(Integer.parseInt(this.vehicles[SwapRouteFrom].getVehicleLoadedCapacity())
							- Integer.parseInt(MovingNodeDemand)));
			this.vehicles[SwapRouteTo].setVehicleRoute(RouteTo.toArray(new Order[RouteTo.size()]));

			this.vehicles[SwapRouteTo].setVehicleLoadedCapacity(
					Integer.toString(Integer.parseInt(this.vehicles[SwapRouteTo].getVehicleLoadedCapacity())
							- Integer.parseInt(MovingNodeDemand)));

			pastSolutions.add(this.distance);

			this.distance += BestNCost;

			if (this.distance < bestSolutionCost) {
				saveBestSolution();
			}

			if (iteration_number == MAX_ITERATIONS) {
				Termination = true;
			}
		}

		this.vehicles = vehiclesForBestSolution;
		this.distance = bestSolutionCost;
		System.out.println("this is vehicles" + this.vehicles);

		try {
			PrintWriter writer = new PrintWriter("PastSolutionsTabu.txt", "UTF-8");
			writer.println("Solutions" + "\t");
			for (int i = 0; i < pastSolutions.size(); i++) {
				writer.println(pastSolutions.get(i) + "\t");
			}
			writer.close();
		} catch (Exception e) {
		}
	}
	
	public Vehicle[] updatedVehicles() {
		return this.vehicles;
	}
	public double updatedDistance() {
		return this.distance;
	}

	public void saveBestSolution() {
		bestSolutionCost = distance;
		for (int j = 0; j < noOfVehicles; j++) {
			Arrays.asList(vehiclesForBestSolution[j].getVehicleRoute()).clear();
			if (!Arrays.asList(vehicles[j].getVehicleRoute()).isEmpty()) {
				int RoutSize = Arrays.asList(vehicles[j].getVehicleRoute()).size();
				for (int k = 0; k < RoutSize; k++) {
					Order orderObj = Arrays.asList(vehicles[j].getVehicleRoute()).get(k);
					Arrays.asList(vehiclesForBestSolution[j].getVehicleRoute()).add(orderObj);
				}
			}

		}

	}

}
