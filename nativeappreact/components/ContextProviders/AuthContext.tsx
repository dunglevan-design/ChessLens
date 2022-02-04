import React, { useContext, useRef, useState } from "react";
import * as Crypto from "expo-crypto";
import { CryptoDigestAlgorithm } from "expo-crypto";
import * as WebBrowser from "expo-web-browser";
import * as Linking from "expo-linking";
import { Alert } from "react-native";

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
    accessToken: "",
  });
  const authstate = useRef("");
  const code_verifier = useRef("");

  const GetUserData = (access_token, expires_in, token_type) => {
    
  }

  const RequestAccessToken = async (code) => {
    let headersList = {
      Accept: "*/*",
      "Content-Type": "application/json",
    };

    let bodyContent = JSON.stringify({
      grant_type: "authorization_code",
      code: code,
      code_verifier: code_verifier.current,
      redirect_uri: "com.nativeappreact://app/home",
      client_id: "nativeappreact",
    });
    const response = await fetch("https://lichess.org/api/token", {
      method: "POST",
      body: bodyContent,
      headers: headersList,
    });
    console.log(response.status);
    if (response.status == 400) {
      throw new Error("Authentication failed, server response with status 400");
    }
    const data = await response.json();
    const { token_type, access_token, expires_in } = data;
    GetUserData(access_token, expires_in, token_type);
  };

  const HandleRedirect = async (url) => {
    console.log("url: >>>", url);
    // WebBrowser.dismissAuthSession();
    let data: Linking.ParsedURL = Linking.parse(url.url);
    console.log("data: >>>", data);

    const { queryParams } = data;
    const { code, state } = queryParams;

    Linking.removeEventListener("url", HandleRedirect);
    if (state === authstate.current) {
      try {
        console.log("requesting token");
        await RequestAccessToken(code);
      } catch (error) {
        console.log(error);
        Alert.alert("Chesslense: ", error.toString());
      }
    }
  };

  const LoginWithLichess = async () => {
    //trigger webbrowser login PKCE flow
    console.log("sign in with Lichess");
    //Create credentials: code challenge, state etc.
    code_verifier.current = Array(128)
      .fill("0")
      .map(() =>
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(
          Math.random() * 62
        )
      )
      .join("");
    console.log(code_verifier.current);
    const digest = await Crypto.digestStringAsync(
      CryptoDigestAlgorithm.SHA256,
      code_verifier.current,
      {
        encoding: Crypto.CryptoEncoding.BASE64,
      }
    );
    const code_challenge = digest
      .replace(/\+/g, "-")
      .replace(/\//g, "_")
      .replace(/=/g, "");
    console.log(code_challenge);
    authstate.current = Array(10)
      .fill("0")
      .map(() => "0123456789".charAt(Math.random() * 10))
      .join("");

    //Open Auth
    Linking.addEventListener("url", HandleRedirect);
    WebBrowser.openAuthSessionAsync(
      `https://lichess.org/oauth?response_type=code&client_id=nativeappreact&redirect_uri=com.nativeappreact://app/home&code_challenge_method=S256&code_challenge=${code_challenge}&state=${authstate.current}`,
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
