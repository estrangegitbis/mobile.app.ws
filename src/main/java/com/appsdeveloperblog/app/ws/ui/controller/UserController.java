package com.appsdeveloperblog.app.ws.ui.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressRest;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  AddressService addressService;

  @Autowired
  ModelMapper modelMapper;

  @GetMapping(path = "/{id}",
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public UserRest getUser(@PathVariable String id) {

    UserRest returnValue = new UserRest();

    UserDto userDto = userService.getUserByUserId(id);

    returnValue = modelMapper.map(userDto, UserRest.class);

    return returnValue;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

    UserRest returnValue = new UserRest();

    if (userDetails.getFirstName().isEmpty()) {
      throw new NullPointerException("The object is null");
    }

    UserDto userDto = modelMapper.map(userDetails, UserDto.class);

    final UserDto createUser = userService.createUser(userDto);

    returnValue = modelMapper.map(createUser, UserRest.class);

    return returnValue;
  }

  @PutMapping(path = "/{id}",
      consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public UserRest updateUser(@PathVariable final String id,
      @RequestBody UserDetailsRequestModel userDetails) {

    UserRest returnValue = new UserRest();

    UserDto userDto = new UserDto();

    userDto = modelMapper.map(userDetails, UserDto.class);

    final UserDto updateUser = userService.updateUser(id, userDto);

    returnValue = modelMapper.map(updateUser, UserRest.class);

    return returnValue;
  }

  @DeleteMapping(path = "/{id}",
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public OperationStatusModel deleteUser(@PathVariable String id) {

    final OperationStatusModel returnValue = new OperationStatusModel();

    userService.deleteUser(id);

    returnValue.setOperationName(RequestOperationName.DELETE.name());
    returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

    return returnValue;
  }

  @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "limit", defaultValue = "2") int limit) {

    List<UserRest> returnValue = new ArrayList<>();

    List<UserDto> users = userService.getUsers(page, limit);

    for (UserDto userDto : users) {
      UserRest userModel = new UserRest();
      userModel = modelMapper.map(userDto, UserRest.class);
      returnValue.add(userModel);
    }

    return returnValue;
  }

  // http://localhost:8080/mobile-app-ws/users/{userId}/addresses
  @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
  public Resources<AddressRest> getUserAddresses(@PathVariable final String id) {

    List<AddressRest> addressesListRestModel = new ArrayList<AddressRest>();

    List<AddressDto> addressesDto = addressService.getAddressesByUserId(id);

    if ((addressesDto != null) && !addressesDto.isEmpty()) {
      Type listType = new TypeToken<List<AddressRest>>() {}.getType();
      addressesListRestModel = modelMapper.map(addressesDto, listType);

      for (AddressRest addressRest : addressesListRestModel) {
        Link addressLink =
            linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
                .withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");

        addressRest.add(addressLink);
        addressRest.add(userLink);
      }
    }

    return new Resources<>(addressesListRestModel);
  }

  @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
  public Resource<AddressRest> getUserAddress(@PathVariable String userId,
      @PathVariable String addressId) {

    AddressDto addressesDto = addressService.getAddressByAddressId(addressId);

    Link addressLink =
        linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
    Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
    Link addressesLink =
        linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

    AddressRest addressRestModel = modelMapper.map(addressesDto, AddressRest.class);

    addressRestModel.add(addressLink);
    addressRestModel.add(userLink);
    addressRestModel.add(addressesLink);

    return new Resource<>(addressRestModel);
  }

  /*
   * http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
   */
  @GetMapping(path = "/email-verification",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

    OperationStatusModel returnValue = new OperationStatusModel();
    returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

    boolean isVerified = userService.verifyEmailToken(token);

    if (isVerified) {
      returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
    } else {
      returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
    }

    return returnValue;
  }
}
