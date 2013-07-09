<?php
	// auth for page
	// pastimerbucky TJxyBSxH
	// sevanaka Z3nbT8ZK
	// escherthelizard9 QKM2JY4v

	$url = "localhost";
	$username = "pluginuser";
	$password = "R6r7UEYy4X26t44E";
	$database = "minecraft";
	
	$dbh = mysql_connect($url,$username,$password);
	
	if (!$dbh) {
		die("Could not connect to database:" . mysql_error());
	}
	
	@mysql_select_db($database);
	
	if (isset($_REQUEST['action'])) {
		$action = $_REQUEST['action'];
		if ($action == "getPrices") {
			$query = "SELECT * FROM prices ORDER BY id DESC";
			$result = mysql_query($query,$dbh);
			
			print "{\"result\":\"success\",\"data\":[";
			$rows = mysql_num_rows($result);
			
			for ($i=0;$i<$rows;$i++) {
				$id = mysql_result($result,$i,"id");
				$name = mysql_result($result,$i,"name");
				$amount = mysql_result($result,$i,"price");
				
				print "{\"id\":$id,\"name\":\"$name\",\"amount\":$amount}";
				if ($i < $rows-1) print ",";
			}
			print "]}";
		} else if ($action == "save") {
			$id = $_REQUEST['id'];
			$name = $_REQUEST['name'];
			$amount = $_REQUEST['amount'];
			$query = "";
			if ($id == -1) {
				$query = "INSERT INTO prices(name,price) VALUES('" . mysql_real_escape_string($name,$dbh) . "'," . mysql_real_escape_string($amount,$dbh) . ")";
			} else {
				$query = "UPDATE prices SET name = '" . mysql_real_escape_string($name,$dbh) . "', price=" . mysql_real_escape_string($amount,$dbh) . " WHERE id = $id";
			}
			
			if (!mysql_query($query,$dbh)) {
				print "{\"result\":\"error\",\"message\":\"Could not save: " . mysql_error($dbh) . "\"}";
			} else {
				print "{\"result\":\"success\"}";
			}
		} else if ($action == "delete") {
			$id = $_REQUEST['id'];
			$query = "DELETE FROM prices WHERE id = " . mysql_real_escape_string($id,$dbh);
			if (!mysql_query($query,$dbh)) {
				print "{\"result\":\"error\",\"message\":\"Could not delete: " . mysql_error($dbh) . "\"}";
			} else {
				print "{\"result\":\"success\"}";
			}			
		} else {
			print "{\"result\":\"error\",\"message\":\"Unknown action: $action\"}";
		}
	} else {
		print "{\"result\":\"error\",\"message\":\"No action provided\"}";
	}
	
	mysql_close($dbh);
?>