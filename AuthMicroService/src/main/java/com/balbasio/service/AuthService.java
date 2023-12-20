package com.balbasio.service;

import com.balbasio.dto.request.LoginRequestDto;
import com.balbasio.dto.request.RegisterRequestDto;
import com.balbasio.exception.AuthException;
import com.balbasio.exception.ErrorType;
import com.balbasio.manager.UserProfileManager;
import com.balbasio.mapper.AuthMapper;
import com.balbasio.rabbitmq.model.CreateUser;
import com.balbasio.rabbitmq.producer.CreateUserProducer;
import com.balbasio.repository.AuthRepository;
import com.balbasio.repository.entity.Auth;
import com.balbasio.utility.JwtTokenManager;
import com.balbasio.utility.enums.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository repository;
    private final UserProfileManager userProfileManager;
    private final JwtTokenManager jwtTokenManager;
    private final CreateUserProducer createUserProducer;
    public void register(RegisterRequestDto dto){
        /**
         * Yeni üye olmak için gelen userName veritabanında kayıtlı olup olmadığını
         * kontrol ediyoruz. Eğer kullanıcı kayıtlı ise hata fırlatıyoruz.
         */
        repository.findOptionalByUserName(dto.getUserName())
                .ifPresent(auth->{
                    throw new AuthException(ErrorType.KAYITLI_KULLANICI_ADI);
                });

        Auth auth = AuthMapper.INSTANCE.fromDto(dto);
        auth.setCreateAt(System.currentTimeMillis());
        auth.setUpdateAt(System.currentTimeMillis());
        auth.setState(State.AKTIF);
        repository.save(auth);
        /**
         * Kullanıcı yeni bir hesap açtığında auth bilgileri kayıt oluyor. Ancak kullanıcı uygulama içinde
         * UserProfile bilgisi ile hareket edecek. Bu nedenle register işleminde kullanıcının profil bilgilerininde
         * oluşturulması gerekli.
         */
//        userProfileManager.save(UserProfileSaveRequestDto.builder()
//                        .authId(auth.getId())
//                        .userName(auth.getUserName())
//                .build());
       // userProfileManager.save(AuthMapper.INSTANCE.fromAuth(auth));
        createUserProducer.converdAndSendMessage(CreateUser.builder()
                        .authId(auth.getId())
                        .userName(auth.getUserName())
                .build());
    }

    public String login(LoginRequestDto dto){
        Optional<Auth> auth = repository.findOptionalByUserNameAndPassword(dto.getUserName(),dto.getPassword());
        if(auth.isEmpty()) throw new AuthException(ErrorType.USERNAME_PASSWORD_ERROR);
        /**
         * Kullanıcının authId bilgisi ile token üretiyoruz. Bu token bilgisini döneceğiz.
         */
        Optional<String> jwtToken = jwtTokenManager.createToken(auth.get().getId());
        if(jwtToken.isEmpty())
            throw new AuthException(ErrorType.TOKEN_ERROR);
        return jwtToken.get();
    }

}
