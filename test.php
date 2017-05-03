<?php 

	if (!isset($_COOKIE['count'])){
		//starts out at 0 instead of 1
		setcookie('count',1);

		//sets cookie count as 1 instead of 0 
		$_COOKIE['count'] = 1;
		
	} else{ //there exists a cookie

		setcookie('count', $_COOKIE['count']+1);

		//corresponding code
		$_COOKIE['count'] = $_COOKIE['count']+1;
	}

	if(isset($_POST['password'])){	
		//is the password pikachu?
		if ($_POST['password']=='pikachu'){

			setcookie('inclasslogin','yes');
			//name and data

			$_COOKIE['inclasslogin'] = 'yes';

			//print "<p> Secret stuff is here </p>";
		} /*else{
			print "<p> Get out! </p>";
		}*/

	}
	//all cookie stuff has to go before the html, otherwise browser wont interpret it as a cookie
?>


<!DOCTYPE html>
<html>
	<head>
		<title> test</title>
	</head>

	<body>
		<h1> password protection </h1>
		<p> you have been here:
		<?php

			print $_COOKIE['count'];
			//the print doesnt show up, only the result is sent to the browser
			?>
		times! </p>

		<?php
			//see if they supplied a password
		//isset = did user set up a password
			if (isset($_COOKIE['inclasslogin']) && $_COOKIE['inclasslogin'] == 'yes'){
				print "Secure stuff means u got password right!";
			}

		/*
		GET shows up in URL, matches the get in form
			if(isset($_GET['password'])){
				//if true, display stuff to user
				/*
				print "You typed in a password!";
				print "That password is: ";
				print $_GET['password'];*/
/*
				//is the password pikachu?
				if ($_GET['password']=='pikachu'){
					print "<p> Secret stuff is here </p>";
				} else{
					print "<p> Get out! </p>";
				}

			}*/

//post doesnt show up in url, has to match with the form
			

		//

		?>
<!--
get method just throws things onto URL
way to have url not display the variables
post method doesnt add to URL
but now our above stuff has to match the post instead of $_GET

-->

	<!--	<form id = "login" method ="get" action="test.php">-->
		<form id = "login" method ="post" action="test.php">
		<!--
		when the user clicks on the button, send all the variables to the file test.php (same file)
		method get means itll be sent thru the url, changes the url, sends the variable into the url?
		sending variables back to itself
		-->
			Password: <input type="text" name="password">
			<input type="submit">
		</form>

	</body>
</html>