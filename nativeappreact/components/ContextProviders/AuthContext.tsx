import React, { useContext, useState } from "react";
import * as Crypto from "expo-crypto";
import { CryptoDigestAlgorithm } from "expo-crypto";
import * as WebBrowser from "expo-web-browser";
import * as Linking from "expo-linking";

const Authcontext = React.createContext({
  user: null,
  LoginWithLichess: async () => {},
  Logout: () => {},
});

const AuthContext: React.FC = ({ children }) => {
  // useAuth here

  const [user, setUser] = useState({
    name: "",
    id: "",
  });

  const HandleRedirect = (e) => {
    console.log("e: >>>", e);
    // WebBrowser.dismissAuthSession();
    let data = Linking.parse(e);
    console.log("data: >>>", data);
  };

  const LoginWithLichess = async () => {
    //trigger webbrowser login PKCE flow
    console.log("sign in with Lichess");
    //Create credentials: code challenge, state etc.
    const code_verifier = Array(128)
      .fill("0")
      .map(() =>
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(
          Math.random() * 62
        )
      )
      .join("");
    console.log(code_verifier);
    const digest = await Crypto.digestStringAsync(
      CryptoDigestAlgorithm.SHA256,
      code_verifier,
      {
        encoding: Crypto.CryptoEncoding.BASE64,
      }
    );
    const code_challenge = digest
      .replace(/\+/g, "-")
      .replace(/\//g, "_")
      .replace(/=/g, "");
    console.log(code_challenge);
    const state = 123;

    //Open Auth
    Linking.addEventListener("url", (url) => HandleRedirect(url));
    WebBrowser.openAuthSessionAsync(
      `https://lichess.org/oauth?response_type=code&client_id=nativeappreact&redirect_uri=com.nativeappreact://app/home&code_challenge_method=S256&code_challenge=${code_challenge}&state=${state}`,
      "nativeappreact://app/game"
    );
  };

  const Logout = () => {
    //invalidate the token
    console.log("log out");
  };

  return (
    <Authcontext.Provider
      value={{ user: null, LoginWithLichess: LoginWithLichess, Logout: Logout }}
    >
      {children}
    </Authcontext.Provider>
  );
};

export const useAuth = () => useContext(Authcontext);

export default AuthContext;
