package biz.netcentric.aem.tools.acvalidator.model;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.day.cq.security.util.CqActions;

import biz.netcentric.aem.tools.acvalidator.model.CqActionTester;


public class CqActionTesterTest {

//	private static final Set<String> CQ_ACTION_SET = new HashSet<String>(Arrays.asList(CqActions.ACTIONS));
//	private Set<String> actionCodesFromTestCase = new HashSet<>();
//	private Map<String, Boolean> actionsMapFromRepository = new HashMap<>();
//
//	@Test
//	public void testSingleAllowedActions(){
//		CqActionTester cqActionsTester = new CqActionTester(null);
//
//		actionCodesFromTestCase.add("read");
//		actionsMapFromRepository = getActionsMap("read");
//
//		assertEquals(cqActionsTester.check(actionCodesFromTestCase, actionsMapFromRepository), true);
//
//		actionCodesFromTestCase.clear();
//		actionCodesFromTestCase.add("read");
//		actionCodesFromTestCase.add("modify");
//		
//		actionsMapFromRepository = getActionsMap("read","modify");
//
//		assertEquals(cqActionsTester.check(actionCodesFromTestCase, actionsMapFromRepository), true);
//
//		actionCodesFromTestCase.clear();
//		actionCodesFromTestCase.add("read");
//		actionsMapFromRepository = getActionsMap("modify");
//		
//		assertEquals(cqActionsTester.check(actionCodesFromTestCase, actionsMapFromRepository), false);
//	}
//
//	@Test
//	public void testFullDeny() {
//		actionCodesFromTestCase.clear();
//		CqActionTester cqActionsTester = new CqActionTester(null);
//
//		actionCodesFromTestCase.add("-");
//		actionsMapFromRepository = getActionsMap();
//		
//		assertEquals(cqActionsTester.check(actionCodesFromTestCase, actionsMapFromRepository), true);
//	}
//
//	@Test
//	public void testFullAllow() {
//		actionCodesFromTestCase.clear();
//		CqActionTester cqActionsTester = new CqActionTester(null);
//
//		actionCodesFromTestCase.add("*");
//		
//		actionsMapFromRepository = getActionsMap("read","modify","create","delete","acl_read","acl_edit","replicate");
//		assertEquals(cqActionsTester.check(actionCodesFromTestCase, actionsMapFromRepository), true);
//	}
//	
//	private Map<String, Boolean> getActionsMap(String... actionCodesFromTestCase){
//		final Map<String, Boolean> actions = new LinkedHashMap<String, Boolean>();
//		List<String> expectedActions = Arrays.asList(actionCodesFromTestCase);
//		for (final String action : CQ_ACTION_SET) {
//			if(expectedActions.contains(action)){
//				actions.put(action, true);
//			}else{
//				actions.put(action, false);
//			}
//		}
//		return actions;
//	}
	
}
