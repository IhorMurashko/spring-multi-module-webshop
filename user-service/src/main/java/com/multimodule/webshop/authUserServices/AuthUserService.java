package com.multimodule.webshop.authUserServices;

import com.multimodule.webshop.dto.UserAuthCredentials;
import com.multimodule.webshop.model.Consumer;
import jakarta.validation.Valid;

public interface AuthUserService {

    boolean registration(@Valid UserAuthCredentials credentials);

    Consumer authentication(@Valid UserAuthCredentials credentials);


}
