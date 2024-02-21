import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Navigationbar } from "@/components/Navigationbar/Navigationbar";
import { Providers } from "@/components/Providers";
import { cookies } from "next/headers";
const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Text2Image web app",
  description: "Text2Image web app",
};

export default async function RootLayout({
  auth,
  children,
}: Readonly<{
  auth: React.ReactNode;
  children: React.ReactNode;
}>) {
  const loggedIn = cookies().has("accessToken");

  return (
    <html lang="en">
      <body className={inter.className}>
        <Providers>
          <Navigationbar isAuthorized={loggedIn}></Navigationbar>
          {auth}
          {children}
        </Providers>
      </body>
    </html>
  );
}
