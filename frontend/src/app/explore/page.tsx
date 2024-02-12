import React from "react";
import { GET } from "../api/models/route";
import { ErrorResponse } from "@/dto/errorResponse";
import { ModelResponse } from "@/dto/modelResponse";
import { CardComponent } from "@/components/CardComponent";
import { jwtDecode } from "jwt-decode";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { Button } from "@nextui-org/react";
import Link from "next/link";

interface JwtToken {
  authorities: string;
}

export default async function Explore() {
  const models: ModelResponse[] | ErrorResponse = await GET();

  const renderCards = Array.isArray(models);

  const jwt = cookies().get("accessToken");

  var isAdmin = false;

  if (jwt) {
    const decodedjwt: JwtToken = jwtDecode(jwt.value);
    if (decodedjwt.authorities.includes("ADMIN")) {
      isAdmin = true;
    }
  }

  if (!renderCards) {
    redirect("/");
  }

  return (
    <>
      {isAdmin ? (
        <div className="flex justify-center items-center mt-10">
          <Link href={"/explore/create"}>
            <Button disableRipple href={"/explore/create"}>
              New model
            </Button>
          </Link>
        </div>
      ) : (
        <></>
      )}
      <div className=" gap-7 mx-64 my-8 grid grid-cols-1 sm:grid-cols-5">
        {models.map((model) => (
          <CardComponent model={model}></CardComponent>
        ))}
      </div>
    </>
  );
}