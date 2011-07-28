#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.POLL;

import javax.servlet.http.HttpSession;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Service
public class HeartbeatService {

	@ExtDirectMethod(value = POLL, event = "heartbeat")
	@PreAuthorize("isAuthenticated()")
	public void heartbeat(HttpSession session) {
		//nothing here
	}
}
