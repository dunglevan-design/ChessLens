import React, { useContext, useState } from "react";
import * as Crypto from "expo-crypto";
import { CryptoDigestAlgorithm } from "expo-crypto";

const Authcontext = React.createContext({
  user: null,
  LoginWithLichess: async() => {},
  Logout: () => {},
});

const AuthContext: React.FC = ({ children }) => {
  // useAuth here

  const [user, setUser] = useState({
    name: "",
    id: "",
  });

  const LoginWithLichess = async () => {
    //trigger webbrowser login PKCE flow
    console.log("sign in with Lichess");
    //Create code challenge
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
