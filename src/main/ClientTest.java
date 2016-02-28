package main;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClientTest {

	@Test
	public void testClient() {
		Client c = new Client();
		assertTrue(true);
	}
	
	@Test
	public void testloginFrame(){
		loginFrame lf = new loginFrame();
		assertTrue(true);
	}
	
	@Test
	public void testchatFrame(){
		String s = "jeremy";
		chatFrame cf = new chatFrame(s);
		assertTrue(true);
	}
	
	

}
