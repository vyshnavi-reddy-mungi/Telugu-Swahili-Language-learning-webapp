package group1.langlearning.controller;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import group1.langlearning.entity.UserRegistration;
import group1.langlearning.models.Constants;
import group1.langlearning.models.GeneralResponse;
import group1.langlearning.models.UserRegistrationRequestModel;
import group1.langlearning.repositories.UserRegistrationRepository;
import group1.langlearning.utils.CommonTasks;

@RestController
@RequestMapping(path="/api/user")
@CrossOrigin("*")
public class UserRegistrationController {
    
   
    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    // @Autowired
    // private CommonTasks commonTasks;

    Gson gson = new GsonBuilder().create(); 

    @PostMapping(value = "/register")
    public ResponseEntity<GeneralResponse> registerUser(@RequestBody String data) {
        
        // ResponseEntity<GeneralResponse> generalResponse = null;
        UserRegistrationRequestModel model = gson.fromJson(data,UserRegistrationRequestModel.class);

        if(data!=null)
        {
        UserRegistration userRegistration = userRegistrationRepository.findByUsername(model.getUsername());
        if(userRegistration != null)
        {
           return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
					Constants.ALREADY_REGISTERED, Constants.ALREADY_REGISTERED_MSG, null), HttpStatus.OK);
        }

        userRegistration = userRegistrationRepository.findByEmail(model.getEmail());
        if(userRegistration != null)
        {
           return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
					Constants.ALREADY_REGISTERED_EMAIL, Constants.ALREADY_REGISTERED_EMAIL_MSG, null), HttpStatus.OK);
        }

        userRegistration = new UserRegistration();
        if(!checkString(model.getUsername()))
            {
           return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
           Constants.USERNAME_NULL, Constants.USERNAME_NULL_MSG, null), HttpStatus.OK);
            }
        if(!checkString(model.getFirstName()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.FIRSTNAME_NULL, Constants.FIRSTNAME_NULL_MSG, null), HttpStatus.OK);
        }
        if(!checkString(model.getLastName()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.LASTNAME_NULL, Constants.LASTNAME_NULL_MSG, null), HttpStatus.OK);
        }
        if(!checkString(model.getEmail()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.EMAIL_NULL, Constants.EMAIL_NULL_MSG, null), HttpStatus.OK);
        }
        if(!checkString(model.getNewPassword()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.NEW_PASSWORD_NULL, Constants.NEW_PASSWORD_NULL_MSG, null), HttpStatus.OK);
        }
        if(!checkString(model.getConfirmPassword()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.CONFIRM_PASSWORD_NULL, Constants.CONFIRM_PASSWORD_NULL_MSG, null), HttpStatus.OK);
        }
        if(!(model.getNewPassword().equals(model.getConfirmPassword())))
        {   
            return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
            Constants.PASSWORD_MISMATCH, Constants.PASSWORD_MISMATCH_MSG, null), HttpStatus.OK);
        }
        if(!checkPassword(model.getNewPassword()))
        {
            return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
            Constants.MISSING_PASSWORD_FORMAT, Constants.MISSING_PASSWORD_FORMAT_MSG, null), HttpStatus.OK);
        }
         
        userRegistration.setUsername(model.getUsername());
        userRegistration.setFirstName(model.getFirstName());
        userRegistration.setLastName(model.getLastName());
        userRegistration.setEmail(model.getEmail());
        userRegistration.setNewPassword(model.getNewPassword());
        userRegistration.setConfirmPassword(model.getConfirmPassword());
        userRegistration.setCreatedTimestamp(new Date());
        userRegistration.setPassword(model.getConfirmPassword());
        userRegistrationRepository.save(userRegistration);
        
       }  
       
       else
       {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.REQUEST_DATA_MISSING, Constants.REQUEST_DATA_MISSING_MSG, null), HttpStatus.OK);
       }

       return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.TRUE,
       Constants.USER_REGISTERED_SUCCESSFULLY, Constants.USER_REGISTERED_SUCCESSFULLY_MSG, null), HttpStatus.OK);

}

@PostMapping(value = "/login")
public ResponseEntity<GeneralResponse> loginUser(@RequestBody String data) {
    
    // ResponseEntity<GeneralResponse> generalResponse = null;
    UserRegistrationRequestModel model = gson.fromJson(data,UserRegistrationRequestModel.class);

    if(data!=null)
    {
    UserRegistration userRegistration = userRegistrationRepository.findByUsernameAndPassword(model.getUsername(),model.getPassword());
    if(userRegistration != null)
    {
       return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.TRUE,
                Constants.LOGIN_SUCCESSFUL, Constants.LOGIN_SUCCESSFUL_MSG, null), HttpStatus.OK);
    }

     userRegistration = userRegistrationRepository.findByUsername(model.getUsername());
    if(userRegistration != null)
    {

       return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
                Constants.INCORRECT_PASSWORD, Constants.INCORRECT_PASSWORDL_MSG, null), HttpStatus.OK);
    }

    else{
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.USER_NOT_FOUND, Constants.USER_NOT_FOUND_MSG, null), HttpStatus.OK);
    
    }
    }
    else
    {
     return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
     Constants.REQUEST_DATA_MISSING, Constants.REQUEST_DATA_MISSING_MSG, null), HttpStatus.OK);
    }
   
}

@PostMapping(value = "/forgot/password")
public ResponseEntity<GeneralResponse> forgotPassword(@RequestBody String data) {
    
    // ResponseEntity<GeneralResponse> generalResponse = null;
    UserRegistrationRequestModel model = gson.fromJson(data,UserRegistrationRequestModel.class);

    if(data!=null)
    {

    UserRegistration userRegistration = userRegistrationRepository.findByUsername(model.getUsername());
    if(userRegistration != null)
    {
        if(!checkString(model.getNewPassword()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.NEW_PASSWORD_NULL, Constants.NEW_PASSWORD_NULL_MSG, null), HttpStatus.OK);
        }
        if(!checkString(model.getConfirmPassword()))
        {
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.CONFIRM_PASSWORD_NULL, Constants.CONFIRM_PASSWORD_NULL_MSG, null), HttpStatus.OK);
        }
        if(!(model.getNewPassword().equals(model.getConfirmPassword())))
        {   
            return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
            Constants.PASSWORD_MISMATCH, Constants.PASSWORD_MISMATCH_MSG, null), HttpStatus.OK);
        }
        if(!checkPassword(model.getNewPassword()))
        {
            return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
            Constants.MISSING_PASSWORD_FORMAT, Constants.MISSING_PASSWORD_FORMAT_MSG, null), HttpStatus.OK);
        }

        userRegistration.setNewPassword(model.getNewPassword());
        userRegistration.setConfirmPassword(model.getConfirmPassword());
        userRegistration.setPassword(model.getConfirmPassword());
        userRegistration.setUpdatedTimestamp(new Date());
        userRegistrationRepository.save(userRegistration);

       return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.TRUE,
                Constants.PASSWORD_CHANGED_SUCCESSFULLY, Constants.PASSWORD_CHANGED_SUCCESSFULLY_MSG, null), HttpStatus.OK);
    }

    else{
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.USER_NOT_FOUND, Constants.USER_NOT_FOUND_MSG, null), HttpStatus.OK);
    
    }
    }
    else
    {
     return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
     Constants.REQUEST_DATA_MISSING, Constants.REQUEST_DATA_MISSING_MSG, null), HttpStatus.OK);
    }
   
}

@PostMapping(value = "/forgot/username")
public ResponseEntity<GeneralResponse> forgotUsername(@RequestBody String data) {
    
    // ResponseEntity<GeneralResponse> generalResponse = null;
    UserRegistrationRequestModel model = gson.fromJson(data,UserRegistrationRequestModel.class);

    if(data!=null)
    {

    UserRegistration userRegistration = userRegistrationRepository.findByEmail(model.getEmail());
    if(userRegistration != null)
    {
        String userName = userRegistration.getUsername();


       return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.TRUE,
                Constants.USER_NAME_FOUND, Constants.USER_NAME_FOUND_MSG, userName), HttpStatus.OK);
    }

    else{
        return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
        Constants.USER_EMAIL_NOT_FOUND, Constants.USER_EMAIL_NOT_FOUND_MSG, null), HttpStatus.OK);
    
    }
    }
    else
    {
     return new ResponseEntity<GeneralResponse>(new GeneralResponse(Constants.FALSE,
     Constants.REQUEST_DATA_MISSING, Constants.REQUEST_DATA_MISSING_MSG, null), HttpStatus.OK);
    }
   
}

 
public boolean checkString(String... data)
{
    if(data == null)
        return false;
    for(String str:data)
    {
        if(str == null)
        return false;
    }
    return true;
}

public boolean checkPassword(String password) {
    String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(password);
    return matcher.matches();
}

}