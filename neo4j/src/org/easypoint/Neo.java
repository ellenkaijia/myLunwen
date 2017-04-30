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
	 * �������߻�ȡ���ݿ�
	 * 
	 * @param path
	 */
	boolean createDB(String path) {
		System.out.println("******�������ȡ���ݿ�ṹ��ʼ,���Ե�******");
		File file = new File(path);
		try {
			if (!file.exists()) { // ����ļ������ڣ���
				file.mkdirs(); // �������ļ���
			}
			this.db = dbFactory.newEmbeddedDatabase(file); // ������ݿ�
			System.out.println("******�������ȡ���ݿ�ṹ���******");
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
	 * ������������ļ�
	 * 
	 * @param lengthOne
	 *            ��һ���ļ��Ľڵ����
	 * @param lengTwo
	 *            �ڶ����ļ��Ľڵ������ϵ����
	 */
	void createRandomFile(int lengthOne, int lengTwo) {

		File file = new File("F:\\neo4j"); // ��һ���ļ�
		File firstExportFile = new File("F:\\ProgramFiles\\Neo4j\\database\\import\\oneTest.csv");
		file.mkdir(); // �����ļ���
		FileWriter fileWriter = null;
		if (firstExportFile.exists()) { // ����ļ�������ɾ��
			firstExportFile.delete();
		}
		try {
			firstExportFile.createNewFile(); // �����µ��ļ�
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
		if (secondExportFile.exists()) { // ����ļ�������ɾ��
			secondExportFile.delete();
		}
		try {
			secondExportFile.createNewFile(); // �����µ��ļ�
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
				"�Ѿ�������������ļ��ȴ����룬·��ΪF:\\ProgramFiles\\Neo4j\\database\\import\\oneTest.csv��F:\\ProgramFiles\\Neo4j\\database\\import\\twoTest.csv");
	}

	/**
	 * ����A*�㷨ר�е��ļ�
	 * 
	 * @param lengthOne
	 * @param lengTwo
	 */
	void createAstarFile(int lengthOne, int lengTwo) {

		File file = new File("F:\\neo4j"); // ��һ���ļ�
		File firstExportFile = new File("F:\\ProgramFiles\\Neo4j\\database\\import\\AstarOneTest.csv");
		file.mkdir(); // �����ļ���
		FileWriter fileWriter = null;
		if (firstExportFile.exists()) { // ����ļ�������ɾ��
			firstExportFile.delete();
		}
		try {
			firstExportFile.createNewFile(); // �����µ��ļ�
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
		if (secondExportFile.exists()) { // ����ļ�������ɾ��
			secondExportFile.delete();
		}
		try {
			secondExportFile.createNewFile(); // �����µ��ļ�
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
				"�Ѿ�������������ļ��ȴ����룬·��ΪF:\\ProgramFiles\\Neo4j\\database\\import\\AstarOneTest.csv��F:\\ProgramFiles\\Neo4j\\database\\import\\AstarTwoTest.csv");
	}

	/**
	 * �������
	 */
	void depthFirst(Scanner scanner) {
		System.out.println("******�����������ǩ�����ڵ����������ֵ ,ÿ�����밴�س�******");
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
				System.err.println("******û���ҵ���Ӧ�Ľڵ�******");
				return;
			}

			TraversalDescription td = this.db.traversalDescription().depthFirst()
					.relationships(RelTypes.CONNECT_TO, Direction.OUTGOING)
					.evaluator(Evaluators.excludeStartPosition());
			Traverser traverser = td.traverse(node);
			useTime = System.currentTimeMillis() - startTime;
			if (traverser == null) {
				System.err.println("******û���ҵ���Ӧ�ļ�¼******");
				return;
			}
			// System.out.println("����ʱ�䣺" + useTime+ "ms");
			total += useTime;
		}
		System.out.println("ƽ������ʱ�䣺" + total / (double) 50.0 + "ms");
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
	 * �������
	 */
	void breadthFirst(Scanner scanner) {
		System.out.println("******�����������ǩ�����ڵ����������ֵ ,ÿ�����밴�س�******");
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
				System.err.println("******û���ҵ���Ӧ�Ľڵ�******");
				return;
			}

			TraversalDescription td = this.db.traversalDescription().breadthFirst()
					.relationships(RelTypes.CONNECT_TO, Direction.OUTGOING)
					.evaluator(Evaluators.excludeStartPosition());
			Traverser traverser = td.traverse(node);
			useTime = System.currentTimeMillis() - startTime;
			if (traverser == null) {
				System.err.println("******û���ҵ���Ӧ�ļ�¼******");
				return;
			}
			// System.out.println("����ʱ�䣺" + useTime + "ms");
			time += useTime;
		}
		System.out.println("ƽ������ʱ�䣺" + time / (double) 50.0 + "ms");
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
	 * ���·��,��Ҫ�����ڵ�
	 */
	void shortestPath(Scanner scanner) {
		System.out.println("******�����������ǩ�����ڵ�1����������ֵ ,ÿ�����밴�س�******");
		String label1 = scanner.next();
		String property1 = scanner.next();
		String value1 = scanner.next();

		System.out.println("******�����������ǩ�����ڵ�2����������ֵ ,ÿ�����밴�س�******");
		String label2 = scanner.next();
		String property2 = scanner.next();
		String value2 = scanner.next();

		int num;
		Long time = 0L;

		Node node1 = this.getNodeByPropertiesValue(label1, property1, value1);
		Node node2 = this.getNodeByPropertiesValue(label2, property2, value2);

		if (node1 == null || node2 == null) {
			System.err.println("******û���ҵ���Ӧ�Ľڵ�******");
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
				System.err.println("******û���ҵ���Ӧ�ļ�¼******");
				return;
			}
			// System.out.println("����ʱ�䣺" + useTime + "ms");

			/*
			 * System.out.println("" + node1.getId() + " " +
			 * node1.getProperty("name")); System.out.println("" + node2.getId()
			 * + " " + node2.getProperty("name")); for (Path shortestPath :
			 * paths) { System.out.println(shortestPath.toString()); }
			 */
			time += useTime;
			// System.out.println();
		}

		System.out.println("ƽ������ʱ�䣺" + time / (double) 50.0 + "ms");
	}

	/**
	 * dijkstra�㷨
	 */
	void dijkstra(Scanner scanner) {
		System.out.println("******�����������ǩ�����ڵ�1����������ֵ ,ÿ�����밴�س�******");
		String label1 = scanner.next();
		String property1 = scanner.next();
		String value1 = scanner.next();

		System.out.println("******�����������ǩ�����ڵ�2����������ֵ ,ÿ�����밴�س�******");
		String label2 = scanner.next();
		String property2 = scanner.next();
		String value2 = scanner.next();
		int num;
		Long time = 0L;

		Node node1 = this.getNodeByPropertiesValue(label1, property1, value1);
		Node node2 = this.getNodeByPropertiesValue(label2, property2, value2);

		if (node1 == null || node2 == null) {
			System.err.println("******û���ҵ���Ӧ�Ľڵ�******");
			return;
		}
	//	System.out.println("" + node1.getId() + " " + node1.getProperty("name"));
	//	System.out.println("" + node2.getId() + " " + node2.getProperty("name"));
		for (num = 1; num <= 50; num++) {

			Long startTime = System.currentTimeMillis();
			Long useTime = 0L;
			// ����dijkstra�㷨
			PathFinder<WeightedPath> finder = GraphAlgoFactory
					.dijkstra(PathExpanders.forTypeAndDirection(RelTypes.CONNECT_TO, Direction.BOTH), "cost");

			WeightedPath path = finder.findSinglePath(node1, node2);

			useTime = System.currentTimeMillis() - startTime;
			if (path == null) {
				System.err.println("******û���ҵ���Ӧ�ļ�¼******");
				return;
			}
			// System.out.println(path.toString());
			// System.out.println("Ȩ�أ�" + path.weight());
			time += useTime;
		}
		System.out.println("ƽ������ʱ�䣺" + time / (double) 50.0 + "ms");
	}

	/**
	 * A*�㷨
	 */
	void aStar(Scanner scanner) {

		System.out.println("******�����������ǩ�����ڵ�1����������ֵ ,ÿ�����밴�س�******");
		String label1 = scanner.next();
		String property1 = scanner.next();
		String value1 = scanner.next();

		System.out.println("******�����������ǩ�����ڵ�2����������ֵ ,ÿ�����밴�س�******");
		String label2 = scanner.next();
		String property2 = scanner.next();
		String value2 = scanner.next();

		Node node1 = this.getNodeByPropertiesValue(label1, property1, value1);
		Node node2 = this.getNodeByPropertiesValue(label2, property2, value2);

		if (node1 == null || node2 == null) {
			System.err.println("******û���ҵ���Ӧ�Ľڵ�******");
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
			// ����A*�㷨
			PathFinder<WeightedPath> astar = GraphAlgoFactory.aStar(
					PathExpanders.forTypeAndDirection(RelTypes.CONNECT_TO, Direction.BOTH),
					CommonEvaluators.doubleCostEvaluator("cost"), estimateEvaluator);

			WeightedPath path = astar.findSinglePath(node1, node2);
			useTime = System.currentTimeMillis() - startTime;
			if (path == null) {
				System.err.println("******û���ҵ���Ӧ�ļ�¼******");
				return;
			}
			time += useTime;
		}
		// System.out.println(path.toString());
		// System.out.println("Ȩ�أ�" + path.weight());
		System.out.println("ƽ������ʱ�䣺" + time / (double) 50.0 + "ms");

	}

	/**
	 * ָ����ȵ���Ȳ�ѯ
	 * 
	 * @param scanner
	 */
	void appointDepthQuery(Scanner scanner) {

		System.out.println("******�����������ǩ�����ڵ����������ֵ ,ÿ�����밴�س�******");
		String label = scanner.next();
		String property = scanner.next();
		String value = scanner.next();
		Node node = this.getNodeByPropertiesValue(label, property, value);

		if (node == null) {
			System.err.println("******û���ҵ���Ӧ�Ľڵ�******");
			return;
		}
		System.out.println("******��������Ҫָ�������******");
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
				System.err.println("******û���ҵ���Ӧ�ļ�¼******");
				return;
			}

			//System.out.println("����ʱ�䣺" + useTime + "ms");
			total += useTime;
		}
		System.out.println("ƽ������ʱ�䣺" + total / (double) 50.0 + "ms");
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

		System.out.println("**********1.�����ļ�  2.������� ***********");
		int howTo = scanner.nextInt();
		if (howTo == 1) {
			System.out.println("*******���뵼���ļ���·��*******");
			String pathM = scanner.next();
			File importFile = new File(pathM);
			if (importFile.exists()) {
				System.out.println("****�Ƿ�ʹ��Ĭ�ϵ����ݿ�·����������1 ��0****");
				int resultInput = scanner.nextInt();

				if (resultInput == 1) {
					System.err.println("------�����������ݿ�,���Ե�------");
					db.shutdown();
					try {
						inserter = BatchInserters.inserter(new File("F://ProgramFiles//Neo4j//database"));
						System.err.println("------�������ݿ����------");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("*****�������µ����ݿ�·��*****");
					String newPath = scanner.next();
					try {
						System.out.println("------�������´������ݿ�,���Ե�------");
						db.shutdown();
						System.err.println("------���ڴ������ݿ�ṹ------");
						inserter = BatchInserters.inserter(new File(newPath));
						System.err.println("------�������ݿ����------");
						Thread.currentThread().sleep(500);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				long genStartTime = 0L;
				long startNode = 0L;
				System.out.println("*******������ڵ����******");
				int propertys = scanner.nextInt();
				System.out.println("*****���봴���ı�ǩ����*****");
				String labelName = scanner.next();
				genStartTime = System.currentTimeMillis(); // ��ʼʱ��

				Label personLabel = Label.label(labelName);
				inserter.createDeferredSchemaIndex(personLabel).on("id").create();

				Map<String, Object> properties = new HashMap<>();

				int i = 1;
				// ��һ�����id
				properties.put("id", i + "");
				i++;
				startNode = inserter.createNode(properties, personLabel);
				System.out.println("�����ĵ�һ�����idΪ:" + startNode);
				for (; i <= propertys; i++) {
					properties.put("id", i + "");
					inserter.createNode(properties, personLabel);
				}
				System.out.println(
						"****����" + propertys + "�ڵ� ����ʱ�䣺" + (System.currentTimeMillis() - genStartTime) + "ms");

				System.out.println("******���ڶ����ϵ�ߣ����Ժ�****");

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
					long relationshipTime = System.currentTimeMillis(); // ��ʼʱ��
					while ((content = bufferReader.readLine()) != null) {
						arr = content.split("\\s+");
						if (arr.length == 0 || arr[0].isEmpty() || arr[1].isEmpty()) {
							continue;
						}
						count++;
						// ��ʼ�����ڵ�Ĺ�ϵ
						long kaishi = Integer.valueOf(arr[0]) + startNode - 1;
						long end = Integer.valueOf(arr[1]) + startNode - 1;

						inserter.createRelationship(kaishi, end, RelTypes.CONNECT_TO, null);
					}
					System.out.println(
							"****����" + count + "�߹�ϵ����ʱ�䣺" + (System.currentTimeMillis() - relationshipTime) + "ms");

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
							System.out.println("*****������ɣ����ڹر����ݿ⣬���Ե�******");
							inserter.shutdown();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} else if (howTo == 2) {
			try {
				System.out.println("*******�����������������ɵĽڵ�������͹�ϵ�ı���******");
				int propertys = scanner.nextInt();
				int relationships = scanner.nextInt();

				System.out.println("****�Ƿ���ʹ��ԭ�������ݿ�·����������1 ��0****");
				int resultInput = scanner.nextInt();

				if (resultInput == 1) {
					System.err.println("------�����������ݿ�,���Ե�------");
					db.shutdown();
					try {
						inserter = BatchInserters.inserter(new File("F://ProgramFiles//Neo4j//database"));
						System.err.println("------�������ݿ����------");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("*****�������µ����ݿ�·��*****");
					String newPath = scanner.next();
					try {
						System.out.println("------�������´������ݿ�,���Ե�------");
						db.shutdown();
						System.err.println("------���ڴ������ݿ�ṹ------");
						inserter = BatchInserters.inserter(new File(newPath));
						System.err.println("------�������ݿ����------");
						Thread.currentThread().sleep(500);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("*****�Ƿ���Ҫ���ɴ��� x,y����Ľڵ�    ��1   ��0*****");
				int isNeedXY = scanner.nextInt();
				Random random = new Random();
				long genStartTime = 0L;
				long startNode = 0L;
				if (isNeedXY == 1) {
					System.out.println("*****���봴���ı�ǩ����*****");
					String labelName = scanner.next();
					genStartTime = System.currentTimeMillis(); // ��ʼʱ��
					Label personLabel = Label.label(labelName);
					inserter.createDeferredSchemaIndex(personLabel).on("id").create();

					Map<String, Object> properties = new HashMap<>();

					int i = 1;
					// ��һ�����id
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
							"****����" + propertys + "�ڵ� ����ʱ�䣺" + (System.currentTimeMillis() - genStartTime) + "ms");
				} else {
					System.out.println("*****���봴���ı�ǩ����*****");
					String labelName = scanner.next();
					genStartTime = System.currentTimeMillis(); // ��ʼʱ��
					Label personLabel = Label.label(labelName);
					inserter.createDeferredSchemaIndex(personLabel).on("id").create();

					Map<String, Object> properties = new HashMap<>();

					int i = 1;
					// ��һ�����id
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
							"****����" + propertys + "�ڵ� ����ʱ�䣺" + (System.currentTimeMillis() - genStartTime) + "ms");
				}

				System.out.println("********���ڴ����ڵ�֮��Ĺ�ϵ�����Ե�*******");
				Map<String, Object> relationMap = new HashMap<>();
				// ��ʼ�����ڵ�Ĺ�ϵ

				long relationshipTime = System.currentTimeMillis();
				for (int j = 1; j <= relationships; j++) {
					long kaishi = random.nextInt(propertys) + startNode;
					long end = random.nextInt(propertys) + startNode;
					double cost = (double) (random.nextInt(99) + 1.0);

					relationMap.put("cost", cost);

					inserter.createRelationship(kaishi, end, RelTypes.BATCH_INSERT, relationMap);
				}
				System.out.println("****����" + relationships + "�߹�ϵ ����ʱ�䣺"
						+ (System.currentTimeMillis() - relationshipTime) + "ms");
			} finally {
				if (inserter != null) {
					System.out.println("*****������ɣ����ڹر����ݿ⣬���Ե�******");
					inserter.shutdown();
				}
			}
		}
	}

	void cypherQuery(Scanner scanner) {
		System.out.println("******������cypher�����в�ѯ,���س�����*****");
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
		System.out.println("��ѯ���Ϊ��" + rows);
	}

	/**
	 * ͨ����������ȡ����ʼ�Ľڵ�
	 * 
	 * @param label
	 *            ��ǩ������
	 * @param property
	 *            ������
	 * @param value
	 *            ֵ
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
	 * A*�㷨����ǰ����ʾ��Ϣ
	 * 
	 * @return
	 */
	int warnning(Scanner scanner) {

		System.out.println("******A*�㷨�ǽ����̬·����������·����Ч�ķ�������ȷ������Ҫ���Ľڵ��а���x��y�����ֵ******");
		System.out.println("******ȷ�� ���� 1���� ���� 0******");
		int input = scanner.nextInt();
		int bbresult = 0;
		if (input == 1) {
			return input;
		} else {
			System.out.println("******�Ƿ���Ҫ��������Ľڵ�͹�ϵ�ļ����е��룬�� 1���� 0******");
			bbresult = scanner.nextInt();
			if (bbresult == 1) {
				System.out.println("������Ҫ������ɵ��ļ���ģ(��һ�����ǽڵ��ģ���ڶ������ǽڵ������ϵ����):");
				int lengthOne = scanner.nextInt();
				int lengTwo = scanner.nextInt();
				this.createAstarFile(lengthOne, lengTwo);
			}
		}
		System.out.println("******�Ƿ��������A*star�㷨�Ĳ���******");
		System.out.println("����1�����У�����0��ر����ݿ⣬Ȼ�������������������ݿ���в��������������������");
		bbresult = scanner.nextInt();
		return bbresult;

	}

	public static void main(String args[]) {
		Neo neo = new Neo(); // ����һ����
		int number[] = new int[2]; // �洢��������
		int isContinue = 1;
		int isContinueTest = 1;
		int isContinueGra = 0;
		Scanner scanner = null;
		int what = 0;
		while (isContinue == 1) {
			try {
				Thread.currentThread().sleep(1000);
				scanner = new Scanner(System.in);
				System.out.println("�Ƿ�Ҫ�����������ڵ��������ļ�(�� 1,�� 0):");
				int isNeed = scanner.nextInt();
				if (isNeed == 1) {
					System.out.println("������Ҫ������ɵ��ļ���ģ(��һ�����ǽڵ��ģ���ڶ������ǽڵ������ϵ����):");
					for (int i = 0; i < 2 && scanner.hasNext(); i++) {
						number[i] = scanner.nextInt();
					}
					neo.createRandomFile(number[0], number[1]); // ������������ļ�
				}
				System.out.println("���ݿ��Ĭ��·����F://ProgramFiles//Neo4j//database");
				System.out.println("�������ݿ�·��");
				String path = scanner.next();
				boolean result = neo.createDB(path); // �������ݿ�
				/*
				 * if (!result) { // ������ݿ��Ҳ�����ʧ�ܣ���ֱ�ӷ���
				 * Thread.currentThread().sleep(1000);
				 * System.out.println("******���ݿ�Ѱ�һ򴴽�ʧ�ܣ����������г���******"); return;
				 * }
				 */
				while (isContinueTest == 1) {
					if (isContinueGra == 0) {
						System.out.println(
								"******������1.��ȱ��� ,2.��ȱ���, 3.���·��, 4. Dijkstra, 5. A*�㷨 , 6. ָ����Ȳ�ѯ , 7.�ر����ݿ�, 8.batchInsert, 9.�������ݿ�********");
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
								System.out.println("���ڹر����ݿ����ӣ�����");
								neo.db.shutdown();
								System.out.println("�������н���������");
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
							System.err.println("------���ڹر����ݿ�------");
							neo.db.shutdown();
							System.exit(0);
							break;
						case 8:
							neo.batchInsert(scanner);
							break;
						case 9:
							System.out.println("***�����������ݿ��·��****");
							String path1 = scanner.next();
							System.out.println("���ݿ��Ĭ��·����F://ProgramFiles//Neo4j//database");
							neo.createDB(path1); // �������ݿ�
							break;
						default:
							System.err.println("------û�������------");
							Thread.currentThread().sleep(1000);
							continue;
						}
						System.out.println("******�Ƿ����ʹ�ø��㷨��(�� 1���� 0�������������¿�ʼ)******");
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
				// ���е���� �ر����ݿ�����
				System.out.println("*******���ڹر����ݿ⣬���Եȣ���Ҫ�رճ���******");
				neo.db.shutdown();
				isContinue = 0; // ��ֵ
				System.out.println("*******�������н���******");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("��������������������������߷����쳣�����ڹر����ݿ⣬���Եȣ�����������");
				if (neo.db != null) {
					neo.db.shutdown();
				}
				System.err.println("�����������������ض��򵽵�һ�������������������");
			}
		}

	}
}
