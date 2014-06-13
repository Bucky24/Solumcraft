<?php
	require_once("setup.php");
	$dbh = mysql_connect($url,$username,$password);
	mysql_select_db($database,$dbh);
	
	if (isset($_REQUEST['action'])) {
		$action = $_REQUEST['action'];
		if ($action === "removePerm") {
			$id = $_REQUEST['id'];
			$query = "DELETE FROM group_permissions WHERE id = " . mysql_real_escape_string($id,$dbh);
			mysql_query($query);
		} else if($action === "addPerm") {
			$group = $_REQUEST['group'];
			$perm = $_REQUEST['perm'];
			$query = "INSERT INTO group_permissions (`group`,permission) VALUES(\"" . mysql_real_escape_string($group,$dbh) . "\",\"" . mysql_real_escape_string($perm,$dbh) . "\")";
			$result = mysql_query($query);
			if (empty($result)) print mysql_error($dbh);
		}
	}
	
	$query = "SELECT * FROM group_permissions ORDER BY `group` ASC, id ASC";
	$result = mysql_query($query,$dbh);
	
	$perms = array();
	for ($i=0;$i<mysql_num_rows($result);$i+=1) {
		$id = mysql_result($result,$i,"id");
		$group = mysql_result($result,$i,"group");
		$perm = mysql_result($result,$i,"permission");
		if (!array_key_exists($group,$perms)) {
			$perms[$group] = array();
		}
		$perms[$group][$id] = $perm;
	}
	
	foreach ($perms as $group=>$permList) {
		print "<h2>$group</h2>";
		foreach ($permList as $id=>$perm) {
			print "$id->$perm <form style='display: inline-block; margin-bottom: 0;' action='perms.php' method='post'><input type='hidden' name='action' value='removePerm'><input type='hidden' name='id' value='$id'><input type='submit' value='Remove'></form><br>";
		}
		print "<form style='display: inline-block; margin-bottom: 0;' action='perms.php' method='post'>";
		print "<input type='hidden' name='action' value='addPerm'>";
		print "<input type='hidden' name='group' value='$group'>";
		print "<input type='text' name='perm'>";
		print "<input type='submit' value='Add Perm'>";
		print "</form><br>";
	}
	
	mysql_close($dbh);
?>