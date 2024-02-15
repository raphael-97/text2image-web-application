import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { JwtPayload, jwtDecode } from "jwt-decode";
import { TokenResponse } from "./dto/tokenResponse";
import { getNewJwtAndRefreshToken } from "./app/lib/authActions";

export async function middleware(request: NextRequest) {
  const accessToken = request.cookies.get("accessToken")?.value;
  const refreshToken = request.cookies.get("refreshToken")?.value;

  if (accessToken && refreshToken) {
    const decoded = jwtDecode<JwtPayload>(accessToken);

    if (decoded.exp) {
      const jwtDate = decoded.exp * 1000;
      if (Date.now() < jwtDate) {
        return NextResponse.next();
      } else {
        try {
          const tokenResponse: TokenResponse = await getNewJwtAndRefreshToken(
            refreshToken
          );

          const response = NextResponse.next();

          await response.cookies.set({
            name: "accessToken",
            value: tokenResponse.accessToken,
            httpOnly: true,
            sameSite: true,
            secure: true,
          });

          await response.cookies.set({
            name: "refreshToken",
            value: tokenResponse.refreshToken,
            httpOnly: true,
            sameSite: true,
            secure: true,
          });
          return response;
        } catch (error) {
          const response = NextResponse.redirect(
            new URL("/login", request.url)
          );
          response.cookies.delete("accessToken");
          response.cookies.delete("refreshToken");
          return response;
        }
      }
    }
  }
  return NextResponse.redirect(new URL("/login", request.url));
}

export const config = {
  matcher: ["/gallery/:path*", "/explore/create/:path*"],
};
