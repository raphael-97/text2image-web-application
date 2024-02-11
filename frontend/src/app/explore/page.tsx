import React from "react";
import { GET } from "../api/models/route";
import { ErrorResponse } from "@/dto/errorResponse";
import { ModelResponse } from "@/dto/modelResponse";
import { Card, CardBody, CardFooter, Image } from "@nextui-org/react";
import { CardComponent } from "@/components/CardComponent";

export default async function Explore() {
  const models: ModelResponse[] | ErrorResponse = await GET();

  console.log(models);

  const renderCards = Array.isArray(models);

  return (
    <>
      {renderCards ? (
        <div className=" gap-7 mx-64 my-8 grid grid-cols-1 sm:grid-cols-5">
          {models.map((model) => (
            <CardComponent model={model}></CardComponent>
          ))}
        </div>
      ) : (
        <div>test2</div>
      )}
    </>
  );
}
