package just.khao.com.controller;

import just.khao.com.model.ResponseMessage;
import just.khao.com.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/test")
    public String TestProfile(){
        return "Profile OK";
    }

    @GetMapping("/retrieve")
    public ResponseMessage findProfile(@RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "") String username){
        ResponseMessage responseMessage = new ResponseMessage();
        if(email != ""){
            try {
                responseMessage.setData(profileService.findByEmail(email));
            } catch(Exception e){
                responseMessage.setStatus(500);
                responseMessage.setMessage("Not Allowed!");
            }
        }
        if(username != ""){
            try {
                responseMessage.setData(profileService.findByUsername(username));
            } catch(Exception e){
                responseMessage.setStatus(500);
                responseMessage.setMessage("Not Allowed!");
            }
        }
        return responseMessage;
    }
}
