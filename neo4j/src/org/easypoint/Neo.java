package org.easypoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

public class Neo {

	public GraphDatabaseFactory dbFactory = null;
	public GraphDatabaseService db = null;

	public Neo() {
		dbFactory = new GraphDatabaseFactory();
	}

	/**
	 * 创建或者获取数据库
	 * 
	 * @param path
	 */
	boolean createDB(String path) {
		System.out.println("******创建或获取数据库结构开始,请稍等******");
		File file = new File(path);
		try {
			if (!file.exists()) { // 如果文件不存在，则
				file.mkdirs(); // 建立新文件夹
			}
			this.db = dbFactory.newEmbeddedDatabase(file); // 获得数据库
			System.out.println("******创建或获取数据库结构完成******");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private static enum RelTypes implements RelationshipType {
		IS_GOING, six, seven, eight, nine, ten, BATCH_INSERT, OTHERBATCH, CONNECT_TO;
	}

	/**
	 * 生成两个随机文件
	 * 
	 * @param lengthOne
	 *            第一个文件的节点个数
	 * @param lengTwo
	 *            第二个文件的节点随机关系个数
	 */
	void createRandomFile(int lengthOne, int lengTwo) {

		File file = new File("F:\\neo4j"); // 第一个文件
		File firstExportFile = new File("F:\\ProgramFiles\\Neo4j\\database\\import\\oneTest.csv");
		file.mkdir(); // 创建文件夹
		FileWriter fileWriter = null;
		if (firstExportFile.exists()) { // 如果文件存在则删除
			firstExportFile.delete();
		}
		try {
			firstExportFile.createNewFile(); // 创建新的文件
			fileWriter = new FileWriter(firstExportFile, false);
			Random random = new Random();
			StringBuffer stringBuffer = new StringBuffer();
			// stringBuffer.append("id" + "," + "weight" + "," + "name" +
			// "\r\n");
			int i = 1;
			for (; i < lengthOne; i++) {
				stringBuffer.append("" + i + ",");
				stringBuffer.append("" + (random.nextInt(999) + 1) + ",");
				stringBuffer.append("\"" + ZcRandomUtil.generateAllLowerString(8) + "\"" + "\r\n");
			}
			stringBuffer.append("" + i + ",");
			stringBuffer.append("" + (random.nextInt(999) + 1) + ",");
			stringBuffer.append("\"" + ZcRandomUtil.generateAllLowerString(8) + "\"");
			fileWriter.write(stringBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		File secondExportFile = new File("F:\\ProgramFiles\\Neo4j\\database\\import\\twoTest.csv");
		if (secondExportFile.exists()) { // 如果文件存在则删除
			secondExportFile.delete();
		}
		try {
			secondExportFile.createNewFile(); // 创建新的文件
			fileWriter = new FileWriter(secondExportFile, false);
			Random random = new Random();
			StringBuffer stringBuffer = new StringBuffer();
			int i = 1;
			for (; i < lengTwo; i++) {
				stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
				stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
				stringBuffer.append("" + (random.nextInt(99) + 1));
				stringBuffer.append("\r\n");
			}
			stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
			stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
			stringBuffer.append("" + (random.nextInt(99) + 1));
			fileWriter.write(stringBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println(
				"已经生成两个随机文件等待导入，路径为F:\\ProgramFiles\\Neo4j\\database\\import\\oneTest.csv，F:\\ProgramFiles\\Neo4j\\database\\import\\twoTest.csv");
	}

	/**
	 * 生成A*算法专有的文件
	 * 
	 * @param lengthOne
	 * @param lengTwo
	 */
	void createAstarFile(int lengthOne, int lengTwo) {

		File file = new File("F:\\neo4j"); // 第一个文件
		File firstExportFile = new File("F:\\ProgramFiles\\Neo4j\\database\\import\\AstarOneTest.csv");
		file.mkdir(); // 创建文件夹
		FileWriter fileWriter = null;
		if (firstExportFile.exists()) { // 如果文件存在则删除
			firstExportFile.delete();
		}
		try {
			firstExportFile.createNewFile(); // 创建新的文件
			fileWriter = new FileWriter(firstExportFile, false);
			Random random = new Random();
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("id" + "," + "weight" + "," + "name" + "," + "x" + "," + "y" + "\r\n");
			int i = 1;
			for (; i < lengthOne; i++) {
				stringBuffer.append("" + i + ",");
				stringBuffer.append("" + (random.nextInt(999) + 1) + ",");
				stringBuffer.append("\"" + ZcRandomUtil.generateAllLowerString(8) + "\"" + ",");
				stringBuffer.append((double) (random.nextInt(98) + 1.0) + ",");
				stringBuffer.append((double) (random.nextInt(98) + 1.0));
				stringBuffer.append("\r\n");
			}
			stringBuffer.append("" + i + ",");
			stringBuffer.append("" + (random.nextInt(999) + 1) + ",");
			stringBuffer.append("\"" + ZcRandomUtil.generateAllLowerString(8) + "\"" + ",");
			stringBuffer.append((double) (random.nextInt(98) + 1.0) + ",");
			stringBuffer.append((double) (random.nextInt(98) + 1.0));
			fileWriter.write(stringBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		File secondExportFile = new File("F:\\ProgramFiles\\Neo4j\\database\\import\\AstarTwoTest.csv");
		if (secondExportFile.exists()) { // 如果文件存在则删除
			secondExportFile.delete();
		}
		try {
			secondExportFile.createNewFile(); // 创建新的文件
			fileWriter = new FileWriter(secondExportFile, false);
			Random random = new Random();
			StringBuffer stringBuffer = new StringBuffer();
			int i = 1;
			for (; i < lengTwo; i++) {
				stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
				stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
				stringBuffer.append((double) (random.nextInt(99) + 1.0));
				stringBuffer.append("\r\n");
			}
			stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
			stringBuffer.append("" + (random.nextInt(lengthOne) + 1) + ",");
			stringBuffer.append((double) (random.nextInt(99) + 1.0));
			fileWriter.write(stringBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println(
				"已经生成两个随机文件等待导入，路径为F:\\ProgramFiles\\Neo4j\\database\\import\\AstarOneTest.csv，F:\\ProgramFiles\\Neo4j\\database\\import\\AstarTwoTest.csv");
	}

	/**
	 * 深度优先
	 */
	void depthFirst(Scanner scanner) {
		System.out.println("******请依次输入标签名，节点的属性名，值 ,每次输入按回车******");
		String label = scanner.next();
		String property = scanner.next();
		String value = scanner.next();
		Long total;
		int i;
		total = 0L;
		for (i = 0; i < 50; i++) {
			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			Node node = this.getNodeByPropertiesValue(label, property, value);

			if (node == null) {
				System.err.println("******没有找到对应的节点******");
				return;
			}

			TraversalDescription td = this.db.traversalDescription().depthFirst()
					.relationships(RelTypes.CONNECT_TO, Direction.OUTGOING)
					.evaluator(Evaluators.excludeStartPosition());
			Traverser traverser = td.traverse(node);
			useTime = System.currentTimeMillis() - startTime;
			if (traverser == null) {
				System.err.println("******没有找到对应的记录******");
				return;
			}
			// System.out.println("消耗时间：" + useTime+ "ms");
			total += useTime;
		}
		System.out.println("平均消耗时间：" + total / (double) 50.0 + "ms");
		/*
		 * int numberOf = 0; String output = ""; for (Path path : traverser) {
		 * output += "At depth " + path.length() + " => [" +
		 * path.endNode().getProperty(property) + " " +
		 * path.endNode().getProperty("name") + " " +
		 * path.endNode().getProperty("weight") + "]\n"; numberOf++; } output +=
		 * "Number of road found: " + numberOf + "\n";
		 * System.out.println(output);
		 */
	}

	/**
	 * 广度优先
	 */
	void breadthFirst(Scanner scanner) {
		System.out.println("******请依次输入标签名，节点的属性名，值 ,每次输入按回车******");
		String label = scanner.next();
		String property = scanner.next();
		String value = scanner.next();
		int num;
		Long time = 0L;

		for (num = 1; num <= 50; num++) {
			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			Node node = this.getNodeByPropertiesValue(label, property, value);

			if (node == null) {
				System.err.println("******没有找到对应的节点******");
				return;
			}

			TraversalDescription td = this.db.traversalDescription().breadthFirst()
					.relationships(RelTypes.CONNECT_TO, Direction.OUTGOING)
					.evaluator(Evaluators.excludeStartPosition());
			Traverser traverser = td.traverse(node);
			useTime = System.currentTimeMillis() - startTime;
			if (traverser == null) {
				System.err.println("******没有找到对应的记录******");
				return;
			}
			// System.out.println("消耗时间：" + useTime + "ms");
			time += useTime;
		}
		System.out.println("平均消耗时间：" + time / (double) 50.0 + "ms");
		/*
		 * int numberOf = 0; String output = ""; for (Path path : traverser) {
		 * output += "At breadth " + path.length() + " => [" +
		 * path.endNode().getProperty(property) + " " +
		 * path.endNode().getProperty("name") + " " +
		 * path.endNode().getProperty("weight") + "]\n"; numberOf++; } output +=
		 * "Number of count found: " + numberOf + "\n";
		 * System.out.println(output);
		 */
	}

	/**
	 * 最短路径,需要两个节点
	 */
	void shortestPath(Scanner scanner) {
		System.out.println("******请依次输入标签名，节点1的属性名，值 ,每次输入按回车******");
		String label1 = scanner.next();
		String property1 = scanner.next();
		String value1 = scanner.next();

		System.out.println("******请依次输入标签名，节点2的属性名，值 ,每次输入按回车******");
		String label2 = scanner.next();
		String property2 = scanner.next();
		String value2 = scanner.next();

		int num;
		Long time = 0L;

		Node node1 = this.getNodeByPropertiesValue(label1, property1, value1);
		Node node2 = this.getNodeByPropertiesValue(label2, property2, value2);

		if (node1 == null || node2 == null) {
			System.err.println("******没有找到对应的节点******");
			return;
		}
		for (num = 1; num <= 50; num++) {

	
			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			PathFinder<Path> finder = GraphAlgoFactory
					.shortestPath(PathExpanders.forTypeAndDirection(RelTypes.CONNECT_TO, Direction.BOTH), 15);
			Iterable<Path> paths = finder.findAllPaths(node1, node2);
			useTime = System.currentTimeMillis() - startTime;
			if (paths == null) {
				System.err.println("******没有找到对应的记录******");
				return;
			}
			// System.out.println("消耗时间：" + useTime + "ms");

			/*
			 * System.out.println("" + node1.getId() + " " +
			 * node1.getProperty("name")); System.out.println("" + node2.getId()
			 * + " " + node2.getProperty("name")); for (Path shortestPath :
			 * paths) { System.out.println(shortestPath.toString()); }
			 */
			time += useTime;
			// System.out.println();
		}

		System.out.println("平均消耗时间：" + time / (double) 50.0 + "ms");
	}

	/**
	 * dijkstra算法
	 */
	void dijkstra(Scanner scanner) {
		System.out.println("******请依次输入标签名，节点1的属性名，值 ,每次输入按回车******");
		String label1 = scanner.next();
		String property1 = scanner.next();
		String value1 = scanner.next();

		System.out.println("******请依次输入标签名，节点2的属性名，值 ,每次输入按回车******");
		String label2 = scanner.next();
		String property2 = scanner.next();
		String value2 = scanner.next();
		int num;
		Long time = 0L;

		Node node1 = this.getNodeByPropertiesValue(label1, property1, value1);
		Node node2 = this.getNodeByPropertiesValue(label2, property2, value2);

		if (node1 == null || node2 == null) {
			System.err.println("******没有找到对应的节点******");
			return;
		}
	//	System.out.println("" + node1.getId() + " " + node1.getProperty("name"));
	//	System.out.println("" + node2.getId() + " " + node2.getProperty("name"));
		for (num = 1; num <= 50; num++) {

			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			// 调用dijkstra算法
			PathFinder<WeightedPath> finder = GraphAlgoFactory
					.dijkstra(PathExpanders.forTypeAndDirection(RelTypes.CONNECT_TO, Direction.BOTH), "cost");

			WeightedPath path = finder.findSinglePath(node1, node2);

			useTime = System.currentTimeMillis() - startTime;
			if (path == null) {
				System.err.println("******没有找到对应的记录******");
				return;
			}
			// System.out.println(path.toString());
			// System.out.println("权重：" + path.weight());
			time += useTime;
		}
		System.out.println("平均消耗时间：" + time / (double) 50.0 + "ms");
	}

	/**
	 * A*算法
	 */
	void aStar(Scanner scanner) {

		System.out.println("******请依次输入标签名，节点1的属性名，值 ,每次输入按回车******");
		String label1 = scanner.next();
		String property1 = scanner.next();
		String value1 = scanner.next();

		System.out.println("******请依次输入标签名，节点2的属性名，值 ,每次输入按回车******");
		String label2 = scanner.next();
		String property2 = scanner.next();
		String value2 = scanner.next();

		Node node1 = this.getNodeByPropertiesValue(label1, property1, value1);
		Node node2 = this.getNodeByPropertiesValue(label2, property2, value2);

		if (node1 == null || node2 == null) {
			System.err.println("******没有找到对应的节点******");
			return;
		}
		// System.out.println("" + node1.getId() + " " +
		// node1.getProperty("name"));
		// System.out.println("" + node2.getId() + " " +
		// node2.getProperty("name"));
		EstimateEvaluator<Double> estimateEvaluator = new EstimateEvaluator<Double>() {
			public Double getCost(final Node node, final Node goal) {
				double dx = Double.valueOf((String) node.getProperty("x"))
						- Double.valueOf((String) goal.getProperty("x"));
				double dy = Double.valueOf((String) node.getProperty("y"))
						- Double.valueOf((String) goal.getProperty("y"));
				double result = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				return result;
			}
		};
		Long time = 0L;
		int i;
		for (i = 1; i <= 50; i++) {
			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			// 调用A*算法
			PathFinder<WeightedPath> astar = GraphAlgoFactory.aStar(
					PathExpanders.forTypeAndDirection(RelTypes.CONNECT_TO, Direction.BOTH),
					CommonEvaluators.doubleCostEvaluator("cost"), estimateEvaluator);

			WeightedPath path = astar.findSinglePath(node1, node2);
			useTime = System.currentTimeMillis() - startTime;
			if (path == null) {
				System.err.println("******没有找到对应的记录******");
				return;
			}
			time += useTime;
		}
		// System.out.println(path.toString());
		// System.out.println("权重：" + path.weight());
		System.out.println("平均消耗时间：" + time / (double) 50.0 + "ms");

	}

	/**
	 * 指定深度的深度查询
	 * 
	 * @param scanner
	 */
	void appointDepthQuery(Scanner scanner) {

		System.out.println("******请依次输入标签名，节点的属性名，值 ,每次输入按回车******");
		String label = scanner.next();
		String property = scanner.next();
		String value = scanner.next();
		Node node = this.getNodeByPropertiesValue(label, property, value);

		if (node == null) {
			System.err.println("******没有找到对应的节点******");
			return;
		}
		System.out.println("******请输入需要指定的深度******");
		int depthCount = scanner.nextInt();
		int i;
		Long total = 0L;
		for (i = 0; i < 50; i++) {
			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			TraversalDescription td = this.db.traversalDescription().depthFirst()
					.relationships(RelTypes.CONNECT_TO, Direction.OUTGOING).evaluator(Evaluators.atDepth(depthCount));
			Traverser traverser = td.traverse(node);
			useTime = System.currentTimeMillis() - startTime;
			if (traverser == null) {
				System.err.println("******没有找到对应的记录******");
				return;
			}

			//System.out.println("消耗时间：" + useTime + "ms");
			total += useTime;
		}
		System.out.println("平均消耗时间：" + total / (double) 50.0 + "ms");
		/*
		 * int numberOf = 0; String output = ""; for (Path path : traverser) {
		 * output += "At depth " + path.length() + " => [" +
		 * path.endNode().getProperty(property) + " " +
		 * path.endNode().getProperty("name") + " " +
		 * path.endNode().getProperty("weight") + "]\n"; numberOf++; } output +=
		 * "Number of count found: " + numberOf + "\n";
		 * System.out.println(output);
		 */
	}

	void batchInsert(Scanner scanner) {
		BatchInserter inserter = null;

		System.out.println("**********1.导入文件  2.随机生成 ***********");
		int howTo = scanner.nextInt();
		if (howTo == 1) {
			System.out.println("*******输入导入文件的路径*******");
			String pathM = scanner.next();
			File importFile = new File(pathM);
			if (importFile.exists()) {
				System.out.println("****是否使用默认的数据库路径创建，是1 否0****");
				int resultInput = scanner.nextInt();

				if (resultInput == 1) {
					System.err.println("------正在重启数据库,请稍等------");
					db.shutdown();
					try {
						inserter = BatchInserters.inserter(new File("F://ProgramFiles//Neo4j//database"));
						System.err.println("------重启数据库完成------");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("*****请输入新的数据库路径*****");
					String newPath = scanner.next();
					try {
						System.out.println("------正在重新创建数据库,请稍等------");
						db.shutdown();
						System.err.println("------正在创建数据库结构------");
						inserter = BatchInserters.inserter(new File(newPath));
						System.err.println("------重启数据库完成------");
						Thread.currentThread().sleep(500);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				long genStartTime = 0L;
				long startNode = 0L;
				System.out.println("*******请输入节点个数******");
				int propertys = scanner.nextInt();
				System.out.println("*****输入创建的标签名字*****");
				String labelName = scanner.next();
				genStartTime = System.currentTimeMillis(); // 开始时间

				Label personLabel = Label.label(labelName);
				inserter.createDeferredSchemaIndex(personLabel).on("id").create();

				Map<String, Object> properties = new HashMap<>();

				int i = 1;
				// 第一个点的id
				properties.put("id", i + "");
				i++;
				startNode = inserter.createNode(properties, personLabel);
				System.out.println("创建的第一个点的id为:" + startNode);
				for (; i <= propertys; i++) {
					properties.put("id", i + "");
					inserter.createNode(properties, personLabel);
				}
				System.out.println(
						"****创建" + propertys + "节点 花费时间：" + (System.currentTimeMillis() - genStartTime) + "ms");

				System.out.println("******正在读入关系边，请稍候****");

				FileInputStream inputStream = null;
				InputStreamReader reader = null;
				BufferedReader bufferReader = null;
				try {
					inputStream = new FileInputStream(importFile);
					reader = new InputStreamReader(inputStream);
					bufferReader = new BufferedReader(reader);
					String content = null;
					String[] arr = null;
					int count = 0;
					long relationshipTime = System.currentTimeMillis(); // 开始时间
					while ((content = bufferReader.readLine()) != null) {
						arr = content.split("\\s+");
						if (arr.length == 0 || arr[0].isEmpty() || arr[1].isEmpty()) {
							continue;
						}
						count++;
						// 开始创建节点的关系
						long kaishi = Integer.valueOf(arr[0]) + startNode - 1;
						long end = Integer.valueOf(arr[1]) + startNode - 1;

						inserter.createRelationship(kaishi, end, RelTypes.CONNECT_TO, null);
					}
					System.out.println(
							"****创建" + count + "边关系花费时间：" + (System.currentTimeMillis() - relationshipTime) + "ms");

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (bufferReader != null) {
							bufferReader.close();
						}
						if (reader != null) {
							reader.close();
						}
						if (inputStream != null) {
							inputStream.close();
						}
						if (inserter != null) {
							System.out.println("*****创建完成，正在关闭数据库，请稍等******");
							inserter.shutdown();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} else if (howTo == 2) {
			try {
				System.out.println("*******请输入依次输入生成的节点个数，和关系的边数******");
				int propertys = scanner.nextInt();
				int relationships = scanner.nextInt();

				System.out.println("****是否还是使用原来的数据库路径创建，是1 否0****");
				int resultInput = scanner.nextInt();

				if (resultInput == 1) {
					System.err.println("------正在重启数据库,请稍等------");
					db.shutdown();
					try {
						inserter = BatchInserters.inserter(new File("F://ProgramFiles//Neo4j//database"));
						System.err.println("------重启数据库完成------");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("*****请输入新的数据库路径*****");
					String newPath = scanner.next();
					try {
						System.out.println("------正在重新创建数据库,请稍等------");
						db.shutdown();
						System.err.println("------正在创建数据库结构------");
						inserter = BatchInserters.inserter(new File(newPath));
						System.err.println("------重启数据库完成------");
						Thread.currentThread().sleep(500);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("*****是否需要生成带有 x,y坐标的节点    是1   否0*****");
				int isNeedXY = scanner.nextInt();
				Random random = new Random();
				long genStartTime = 0L;
				long startNode = 0L;
				if (isNeedXY == 1) {
					System.out.println("*****输入创建的标签名字*****");
					String labelName = scanner.next();
					genStartTime = System.currentTimeMillis(); // 开始时间
					Label personLabel = Label.label(labelName);
					inserter.createDeferredSchemaIndex(personLabel).on("id").create();

					Map<String, Object> properties = new HashMap<>();

					int i = 1;
					// 第一个点的id
					properties.put("id", i + "");
					properties.put("weight", random.nextInt(999) + 1);
					properties.put("name", ZcRandomUtil.generateAllLowerString(8));
					properties.put("x", (double) (random.nextInt(98) + 1.0));
					properties.put("y", (double) (random.nextInt(98) + 1.0));
					i++;

					startNode = inserter.createNode(properties, personLabel);

					for (; i <= propertys; i++) {
						properties.put("id", i + "");
						properties.put("weight", random.nextInt(999) + 1);
						properties.put("name", ZcRandomUtil.generateAllLowerString(8));
						properties.put("x", (double) (random.nextInt(98) + 1.0));
						properties.put("y", (double) (random.nextInt(98) + 1.0));
						inserter.createNode(properties, personLabel);
					}
					System.out.println(
							"****创建" + propertys + "节点 花费时间：" + (System.currentTimeMillis() - genStartTime) + "ms");
				} else {
					System.out.println("*****输入创建的标签名字*****");
					String labelName = scanner.next();
					genStartTime = System.currentTimeMillis(); // 开始时间
					Label personLabel = Label.label(labelName);
					inserter.createDeferredSchemaIndex(personLabel).on("id").create();

					Map<String, Object> properties = new HashMap<>();

					int i = 1;
					// 第一个点的id
					properties.put("id", i + "");
					properties.put("weight", random.nextInt(999) + 1);
					properties.put("name", ZcRandomUtil.generateAllLowerString(8));
					i++;

					startNode = inserter.createNode(properties, personLabel);

					for (; i <= propertys; i++) {
						properties.put("id", i + "");
						properties.put("weight", random.nextInt(999) + 1);
						properties.put("name", ZcRandomUtil.generateAllLowerString(8));
						inserter.createNode(properties, personLabel);
					}
					System.out.println(
							"****创建" + propertys + "节点 花费时间：" + (System.currentTimeMillis() - genStartTime) + "ms");
				}

				System.out.println("********正在创建节点之间的关系，请稍等*******");
				Map<String, Object> relationMap = new HashMap<>();
				// 开始创建节点的关系

				long relationshipTime = System.currentTimeMillis();
				for (int j = 1; j <= relationships; j++) {
					long kaishi = random.nextInt(propertys) + startNode;
					long end = random.nextInt(propertys) + startNode;
					double cost = (double) (random.nextInt(99) + 1.0);

					relationMap.put("cost", cost);

					inserter.createRelationship(kaishi, end, RelTypes.BATCH_INSERT, relationMap);
				}
				System.out.println("****创建" + relationships + "边关系 花费时间："
						+ (System.currentTimeMillis() - relationshipTime) + "ms");
			} finally {
				if (inserter != null) {
					System.out.println("*****创建完成，正在关闭数据库，请稍等******");
					inserter.shutdown();
				}
			}
		}
	}

	void cypherQuery(Scanner scanner) {
		System.out.println("******请输入cypher语句进行查询,按回车结束*****");
		String cypher = scanner.next();
		String rows = "";
		try (Transaction ignored = db.beginTx(); Result result = db.execute(cypher)) {
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				for (Entry<String, Object> column : row.entrySet()) {
					rows += column.getKey() + ": " + column.getValue() + "; ";
				}
				rows += "\n";
			}
		}
		System.out.println("查询结果为：" + rows);
	}

	/**
	 * 通过属性来获取到开始的节点
	 * 
	 * @param label
	 *            标签的名字
	 * @param property
	 *            属性名
	 * @param value
	 *            值
	 * @return
	 */
	Node getNodeByPropertiesValue(String label, String property, String value) {

		Label moLable = Label.label(label);
		Transaction tx = this.db.beginTx();
		ResourceIterator<Node> nodeResult = this.db.findNodes(moLable, property, value);
		tx.success();
		return nodeResult.next();

	}

	/**
	 * A*算法调用前的提示消息
	 * 
	 * @return
	 */
	int warnning(Scanner scanner) {

		System.out.println("******A*算法是解决静态路网中求解最短路最有效的方法，请确保你所要检测的节点中包含x，y坐标的值******");
		System.out.println("******确定 输入 1，否定 输入 0******");
		int input = scanner.nextInt();
		int bbresult = 0;
		if (input == 1) {
			return input;
		} else {
			System.out.println("******是否需要生成随机的节点和关系文件进行导入，是 1，否 0******");
			bbresult = scanner.nextInt();
			if (bbresult == 1) {
				System.out.println("请输入要随机生成的文件规模(第一个数是节点规模，第二个数是节点随机关系个数):");
				int lengthOne = scanner.nextInt();
				int lengTwo = scanner.nextInt();
				this.createAstarFile(lengthOne, lengTwo);
			}
		}
		System.out.println("******是否继续运行A*star算法的测试******");
		System.out.println("输入1则运行，输入0会关闭数据库，然后可以重启浏览器对数据库进行操作，都不想则随便输入");
		bbresult = scanner.nextInt();
		return bbresult;

	}

	public static void main(String args[]) {
		Neo neo = new Neo(); // 定义一个类
		int number[] = new int[2]; // 存储两个数字
		int isContinue = 1;
		int isContinueTest = 1;
		int isContinueGra = 0;
		Scanner scanner = null;
		int what = 0;
		while (isContinue == 1) {
			try {
				Thread.currentThread().sleep(1000);
				scanner = new Scanner(System.in);
				System.out.println("是否要生成两个用于导入的随机文件(是 1,否 0):");
				int isNeed = scanner.nextInt();
				if (isNeed == 1) {
					System.out.println("请输入要随机生成的文件规模(第一个数是节点规模，第二个数是节点随机关系个数):");
					for (int i = 0; i < 2 && scanner.hasNext(); i++) {
						number[i] = scanner.nextInt();
					}
					neo.createRandomFile(number[0], number[1]); // 生成两个随机文件
				}
				System.out.println("数据库的默认路径：F://ProgramFiles//Neo4j//database");
				System.out.println("输入数据库路径");
				String path = scanner.next();
				boolean result = neo.createDB(path); // 创建数据库
				/*
				 * if (!result) { // 如果数据库找不到或失败，则直接返回
				 * Thread.currentThread().sleep(1000);
				 * System.out.println("******数据库寻找或创建失败，请重新运行程序******"); return;
				 * }
				 */
				while (isContinueTest == 1) {
					if (isContinueGra == 0) {
						System.out.println(
								"******请输入1.深度遍历 ,2.广度遍历, 3.最短路径, 4. Dijkstra, 5. A*算法 , 6. 指定深度查询 , 7.关闭数据库, 8.batchInsert, 9.重连数据库********");
						what = scanner.nextInt();
					}
					try {
						switch (what) {
						case 1:
							neo.depthFirst(scanner);
							break;
						case 2:
							neo.breadthFirst(scanner);
							break;
						case 3:
							neo.shortestPath(scanner);
							break;
						case 4:
							neo.dijkstra(scanner);
							break;
						case 5:
							if (neo.warnning(scanner) == 1) {
								neo.aStar(scanner);
							} else {
								System.out.println("正在关闭数据库连接！！！");
								neo.db.shutdown();
								System.out.println("程序运行结束！！！");
								System.exit(0);
							}
							break;
						/*
						 * case 6: neo.cypherQuery(scanner); break;
						 */
						case 6:
							neo.appointDepthQuery(scanner);
							break;
						case 7:
							System.err.println("------正在关闭数据库------");
							neo.db.shutdown();
							System.exit(0);
							break;
						case 8:
							neo.batchInsert(scanner);
							break;
						case 9:
							System.out.println("***输入重连数据库的路径****");
							String path1 = scanner.next();
							System.out.println("数据库的默认路径：F://ProgramFiles//Neo4j//database");
							neo.createDB(path1); // 创建数据库
							break;
						default:
							System.err.println("------没有这项功能------");
							Thread.currentThread().sleep(1000);
							continue;
						}
						System.out.println("******是否继续使用该算法？(是 1，否 0，输入错误会重新开始)******");
						scanner = new Scanner(System.in);
						isContinueGra = scanner.nextInt();
						if (isContinueGra == 1) {
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
						Thread.currentThread().sleep(1000);
					}
				}
				// 运行到最后 关闭数据库连接
				System.out.println("*******正在关闭数据库，请稍等，不要关闭程序******");
				neo.db.shutdown();
				isContinue = 0; // 赋值
				System.out.println("*******程序运行结束******");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("！！！！！！您的输入有误或者发生异常，正在关闭数据库，请稍等！！！！！！");
				if (neo.db != null) {
					neo.db.shutdown();
				}
				System.err.println("！！！！！！重新重定向到第一步骤操作！！！！！！");
			}
		}

	}
}
