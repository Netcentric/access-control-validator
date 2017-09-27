package biz.netcentric.aem.tools.acvalidator.service;

import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.apache.sling.settings.SlingSettingsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests ACValidatorServiceImpl.
 * 
 * @author Roland Gruber
 */
@RunWith(value=MockitoJUnitRunner.class)
public class ACValidatorServiceImplTest {
	
	@Mock
	SlingSettingsService settings;

	@Spy
	@InjectMocks
	ACValidatorServiceImpl service;
	
	@Test
	public void testMatchesRunMode() {
		Set<String> runModes = new HashSet<>();
		runModes.add("author");
		runModes.add("dev");
		when(settings.getRunModes()).thenReturn(runModes);
		
		assertTrue(service.matchesRunMode("tests.author"));
		assertTrue(service.matchesRunMode("tests.author.dev"));
		assertTrue(service.matchesRunMode("tests.yaml"));
		assertTrue(service.matchesRunMode("tests.publish.yaml"));
		assertFalse(service.matchesRunMode("tests.publish"));
		assertFalse(service.matchesRunMode("tests.author.local"));
	}
	
}
