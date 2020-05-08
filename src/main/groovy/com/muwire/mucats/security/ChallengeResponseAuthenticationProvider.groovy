package com.muwire.mucats.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

import com.muwire.core.Constants

import net.i2p.crypto.DSAEngine
import net.i2p.data.Base64
import net.i2p.data.Signature

class ChallengeResponseAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ChallengeResponseAuthentication cra = authentication
        
        byte [] response = Base64.decode(cra.getResponse())
        if (response == null)
            throw new AuthenticationException("base64 couldn't decode response") {}
        
        def sig = new Signature(Constants.SIG_TYPE, response)
        def spk = cra.getPersona().getDestination().getSigningPublicKey()
        if (DSAEngine.getInstance().verifySignature(sig, cra.getChallenge(), spk)) {
            authentication.setAuthenticated(true)
            cra.setRoles("ROLE_USER") // TODO: check with db and stuff
            return cra
        }else
            throw new AuthenticationException("invalid response") {}
    }

    @Override
    public boolean supports(Class<?> authentication) {
        authentication == ChallengeResponseAuthentication.class
    }

}
