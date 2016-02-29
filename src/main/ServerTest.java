package main;

import static org.junit.Assert.*;

import java.net.Socket;

import org.junit.Test;

public class ServerTest {

	@Test
	public void testStart() {
		Server s = new Server();
		s.start();
		assertTrue(true);
	}
}
