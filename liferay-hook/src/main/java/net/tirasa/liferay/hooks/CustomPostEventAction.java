package net.tirasa.liferay.hooks;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomPostEventAction extends Action {

    private static final Log LOGGER = LogFactoryUtil.getLog(CustomPostEventAction.class);
    
    @Override
    public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {

        HashMap<String, String> principal = (HashMap) request.getAttribute("principal");
        
        LOGGER.debug("Passing principal to current session");
        try {
            request.getSession().setAttribute("principal", principal); 
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
