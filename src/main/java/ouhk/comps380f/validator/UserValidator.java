package ouhk.comps380f.validator;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ouhk.comps380f.controller.TicketUserController.Form;
import ouhk.comps380f.dao.TicketUserRepository;
import ouhk.comps380f.model.TicketUser;

@Component
public class UserValidator implements Validator {

    @Resource
    TicketUserRepository ticketUserRepo;

    //check type is it equal type
    @Override
    public boolean supports(Class<?> type) {
        return Form.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        //cast o to from type
        Form user = (Form) o;
        //
        ValidationUtils.rejectIfEmpty(errors, "confirm_password", "",
                "Please confirm your password.");
        if (!user.getPassword().equals(user.getConfirm_password())) {
            errors.rejectValue("confirm_password", "",
                    "Password does not match.");
        }
      
        TicketUser ticketUser = ticketUserRepo.findOne(user.getUsername());
        if (ticketUser != null) {
            errors.rejectValue("username", "", "User already exists.");
        }
    }
}
