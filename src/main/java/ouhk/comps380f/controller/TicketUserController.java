package ouhk.comps380f.controller;

import java.io.IOException;
import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import ouhk.comps380f.dao.TicketUserRepository;
import ouhk.comps380f.model.TicketUser;
import ouhk.comps380f.validator.UserValidator;



@Controller
@RequestMapping("user")
public class TicketUserController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Resource
    TicketUserRepository ticketUserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

   @Autowired
private UserValidator userValidator;

    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String list(ModelMap model) {
        model.addAttribute("ticketUsers", ticketUserRepo.findAll());
        return "listUser";
    }

    public static class Form {

        //user name can not empty, if empty message pop out
        @NotEmpty(message = "Please enter your user name")
        private String username;
        @NotEmpty(message = "Please enter your password")
        @Size(min = 6, max = 15, message = "Your password must between {min} and {max} characters")
        private String password;

        private String confirm_password;

        @NotEmpty(message = "Please select your role")
        private String[] roles;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String[] getRoles() {
            return roles;
        }

        public void setRoles(String[] roles) {
            this.roles = roles;
        }

        public String getConfirm_password() {
            return confirm_password;
        }

        public void setConfirm_password(String confirm_password) {
            this.confirm_password = confirm_password;
        }

    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create() {
        return new ModelAndView("addUser", "ticketUser", new Form());
    }

    //
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@ModelAttribute("ticketUser") @Valid Form form, BindingResult result) throws IOException {
        userValidator.validate(form, result);
        if (result.hasErrors()) {
            return "addUser";
        }
        TicketUser user = new TicketUser(form.getUsername(),
                passwordEncoder.encode(form.getPassword()),
                form.getRoles()
        );
        ticketUserRepo.save(user);
        logger.info("User " + form.getUsername() + " created.");
        return "redirect:/user/list";
    }

    @RequestMapping(value = "delete/{username}", method = RequestMethod.GET)
    public View deleteTicket(@PathVariable("username") String username) {
        ticketUserRepo.delete(ticketUserRepo.findOne(username));
        logger.info("User " + username + " deleted.");
        return new RedirectView("/user/list", true);
    }
}
