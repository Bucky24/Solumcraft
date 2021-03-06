<?php
	// expects $url, $username, $password, $database
	include_once("setup.php");
	
	$dbh = mysql_connect($url,$username,$password);
	
	if (!$dbh) {
		die("Could not connect to database:" . mysql_error());
	}
	
	@mysql_select_db($database);
	
	if (isset($_REQUEST['action'])) {
		$action = $_REQUEST['action'];
		
		if ($action == "getChat") {
			$lastId = $_REQUEST['id'];
			$history = 0;
			
			if (isset($_REQUEST['history'])) {
				$history = $_REQUEST['history'];
			}
		
			if ($lastId == -1) {
				$query = "SELECT MAX(id) AS id FROM chat";
				$result = mysql_query($query,$dbh);
				$lastId = mysql_result($result,0,"id")-1;
			}
			//$active = 0;
			$activePlayers = "[";
			$playerList = array();
			
			$query = "SELECT distinct player FROM player_login order by id desc";
			$result = mysql_query($query,$dbh);
			for ($i=0;$i<mysql_num_rows($result);$i+=1) {
				$player = mysql_result($result,$i,"player");
				$query = "SELECT event FROM player_login WHERE player = '$player' ORDER BY `date` DESC LIMIT 1";
				$result2 = mysql_query($query,$dbh);
				if (mysql_num_rows($result2) > 0) {
					$event = mysql_result($result2,0,"event");
					if ($event == "login") {
						//$active += 1;
						$playerList[] = $player;
					}
				}
			}
			for ($i=0;$i<count($playerList);$i+=1) {
				$player = $playerList[$i];
				$activePlayers .= "\"$player\"";
				if ($i < count($playerList)-1) {
					$activePlayers .= ",";
				}
			}
			$activePlayers .= "]";
		
			$query = "SELECT * FROM chat WHERE id > $lastId-$history order by id ASC";
			$result = mysql_query($query,$dbh);
			
			print "{\"success\":true,\"data\":[";
			
			for ($i=0;$i<mysql_num_rows($result);$i+=1) {
				$id = mysql_result($result,$i,"id");
				$player = mysql_result($result,$i,"player");
				$message = mysql_result($result,$i,"message");
				$time = mysql_result($result,$i,"time");
				$playerNice = mysql_result($result,$i,"player_string");
				
				$message = str_replace("\\","\\\\",$message);
				$message = str_replace("\"","\\\"",$message);
				
				if ($playerNice == "") { 
					$playerNice = $player;
				}
				
				print "{\"id\":$id,\"player\":\"$player\",\"message\":\"$message\",\"time\":\"$time\",\"player_string\":\"$playerNice\"}";
				
				if ($i < mysql_num_rows($result)-1) {
					print ",";
				}
			}
			
			print "],\"activePlayers\":$activePlayers";
			
			print ",\"activeWeb\":[";
			
			$query = "SELECT player FROM session WHERE expires > NOW() group by player";
			$result = mysql_query($query,$dbh);
			
			for ($i=0;$i<mysql_num_rows($result);$i+=1) {
				$player = mysql_result($result,$i,"player");
				print "\"$player\"";
				if ($i < mysql_num_rows($result)-1) {
					print ",";
				}
			}
			
			print "]";
			print "}";
			
		} else if ($action == "sendMessage") {
			if (!isset($_REQUEST['message'])) {
				print "{\"message\":\"Required parameter message not given\"}";
				exit(1);
			}
			if (!isset($_REQUEST['username'])) {
				print "{\"message\":\"Required parameter username not given\"}";
				exit(1);
			}
			if (!isset($_REQUEST['session'])) {
				print "{\"message\":\"Required parameter session not given\"}";
				exit(1);
			}
			$message = $_REQUEST['message'];
			$user = $_REQUEST['username'];
			$session = $_REQUEST['session'];
			
			if (isSessionValid($user,$session)) {
				updateSession($session);
				$message = urldecode($message);

				$title = "";
				$query = "SELECT title FROM title WHERE player = '" . mysql_real_escape_string($user) . "'";
				$result = mysql_query($query,$dbh);
				if (mysql_num_rows($result) > 0) {
					$title = mysql_result($result,0,"title");
					$title .= ":white: ";
				}
				
				$title .= "$user";
				
				$query = "INSERT INTO chat(message,player,time,seen,player_string) VALUES('" . mysql_real_escape_string($message) . "','" . mysql_real_escape_string($user) . "',now(),0,'" . mysql_real_escape_string($title) . "')";
				//error_log($query);
				mysql_query($query,$dbh);
				print "{\"success\":true,\"timeout\":" . getSessionTime($user,$session) . "}";
			} else {
				print "{\"message\":\"Your session has expired, please login again\"}";
			}
		} else if ($action == "login") {
			if (!isset($_REQUEST['username'])) {
				print "{\"message\":\"Required parameter username not given\"}";
				exit(1);
			}
			if (!isset($_REQUEST['password'])) {
				print "{\"message\":\"Required parameter password not given\"}";
				exit(1);
			}
			$user = $_REQUEST['username'];
			$pass = $_REQUEST['password'];
			
			$query = "SELECT COUNT(*) AS `count` FROM login WHERE player = '" . mysql_real_escape_string($user) . "' AND password = '" . mysql_real_escape_string($pass) . "'";
			$result = mysql_query($query,$dbh);
			$count = mysql_result($result,0,"count");
			if ($count < 1) {
				print "{\"message\":\"Invalid login\"}";
				exit(1);
			} else {
				$session = getSession($user);
				if (!isSessionValid($user,$session)) {
					$query = "INSERT INTO session(player,created,expires) VALUES('" . mysql_real_escape_string($user) . "',NOW(),NOW() + interval 20 minute)";
					if (!mysql_query($query,$dbh)) {
						print "{\"message\":\"Unable to login " . mysql_error() . "\"}";
						exit(1);
					}
					$session = mysql_insert_id($dbh);
				} else {
					updateSession($session);
				}
				
				print "{\"success\":true,\"session\":$session}";
			}
		} else if ($action == "timeLeft") {
			if (!isset($_REQUEST['username'])) {
				print "{\"message\":\"Required parameter username not given\"}";
				exit(1);
			}
			if (!isset($_REQUEST['session'])) {
				print "{\"message\":\"Required parameter session not given\"}";
				exit(1);
			}
			$message = $_REQUEST['message'];
			$user = $_REQUEST['username'];
			$session = $_REQUEST['session'];
	
			if (isSessionValid($user,$session)) {
				$time = getSessionTime($user,$session);
				print "{\"success\":true,\"time\":\"$time\"}";
			} else {
				print "{\"success\":true,\"time\":\"0\"}";
			}
		} else if ($action == "updateSession") {
			if (!isset($_REQUEST['username'])) {
				print "{\"message\":\"Required parameter username not given\"}";
				exit(1);
			}
			if (!isset($_REQUEST['session'])) {
				print "{\"message\":\"Required parameter session not given\"}";
				exit(1);
			}
			$message = $_REQUEST['message'];
			$user = $_REQUEST['username'];
			$session = $_REQUEST['session'];
	
			if (isSessionValid($user,$session)) {
				updateSession($session);
				print "{\"success\":true}";
			} else {
				print "{\"success\":false,\"message\":\"Session already expired\"}";
			}
		} else {
			print "{\"message\":\"Action $action is unknown\"}";
		}
	} else {
		print "{\"message\":\"Action not given\"}";
	}
	
	mysql_close($dbh);
	
	function getSession($username) {
		global $dbh;
		$query = "SELECT id FROM session WHERE player = '" . mysql_real_escape_string($username) . "' AND expires > NOW()";
		$result = mysql_query($query,$dbh);
		if (mysql_num_rows($result) > 0) {
			return mysql_result($result,0,"id");
		} else {
			return -1;
		}
	}
	
	function isSessionValid($username,$session) {
		if ($session == -1 || $session == "") return false;
		global $dbh;
		$query = "SELECT COUNT(*) as `count` FROM session WHERE id = " . mysql_real_escape_string($session) . " AND player = '" . mysql_real_escape_string($username) . "' AND expires > NOW()";
		$result = mysql_query($query,$dbh);
		if (!$result) {
			error_log($query . " " . mysql_error());
			return false;
		}
		$count = mysql_result($result,0,"count");
		
		return ($count > 0);
	}
	
	function getSessionTime($username,$session) {
		if ($session == -1 || $session == "") return false;
		global $dbh;
		$query = "SELECT expires FROM session WHERE id = " . mysql_real_escape_string($session) . " AND player = '" . mysql_real_escape_string($username) . "'";
		$result = mysql_query($query,$dbh);
		if (!$result) {
			error_log($query . " " . mysql_error());
			return -1;
		}
		$count = strtotime(mysql_result($result,0,"expires"));
		
		$now = time();
		
		return $count-$now;
	}
	
	function updateSession($session) {
		global $dbh;
		$query = "UPDATE session SET expires = NOW() + interval 20 minute WHERE id = " . mysql_real_escape_string($session);
		mysql_query($query,$dbh);		
	}
?>