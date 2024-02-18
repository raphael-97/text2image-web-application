import React from "react";
import { notFound } from "next/navigation";
import { ModelResponse } from "@/dto/modelResponse";
import ModelView from "@/components/ModelView";
import { GET } from "@/app/api/models/route";

export default async function ModelPage({
  params: { id },
}: {
  params: { id: string };
}) {
  const res = await GET();
  const models: ModelResponse[] = await res.json();

  const modelName = models.find((model) => model.id === parseInt(id))?.name;
  if (!modelName) notFound();

  return (
    <div className="flex flex-col text-center mt-10 items-center">
      <h1 className=" text-3xl mb-10">{modelName}</h1>
      <ModelView modelId={id} />
    </div>
  );
}
