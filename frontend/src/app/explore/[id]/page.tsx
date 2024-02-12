"use client";
import { Button, Textarea } from "@nextui-org/react";
import Image from "next/image";
import React, { useState } from "react";
import { CiImageOn } from "react-icons/ci";
import { useSearchParams } from "next/navigation";
import { GrGallery } from "react-icons/gr";
import { IoDownloadOutline } from "react-icons/io5";

export default function page({ params: { id } }: { params: { id: string } }) {
  const searchParams = useSearchParams();
  const modelName = searchParams.get("name");
  const [value, setValue] = useState<string>("");
  const [imageUrl, setImageUrl] = useState<string>("");
  const [imageBlob, setImageBlob] = useState<Blob>();
  const [loading, setIsLoading] = useState<boolean>(false);

  const generateImage = async () => {
    setIsLoading(true);
    const resData = await fetch(`/api/models/${id}`, {
      method: "POST",
      body: JSON.stringify(value),
    });

    if (!resData.ok) {
      await setImageUrl("");
      setIsLoading(false);
      return;
    }

    await resData.blob().then((blob) => {
      setImageUrl(URL.createObjectURL(blob));
      setImageBlob(blob);
    });

    setIsLoading(false);
  };

  const uploadToGallery = async () => {
    if (imageBlob) {
      const imageFile = new File([imageBlob], "image.jpg");

      const sendFormData = new FormData();

      sendFormData.append("file", imageFile, imageFile.name);

      await fetch(`/api/images`, {
        method: "POST",
        body: sendFormData,
      });
    }
  };

  return (
    <div className="flex flex-col text-center mt-10 items-center">
      <h1 className=" text-3xl mb-10">{modelName}</h1>
      <div>
        {imageUrl ? (
          <div className="relative w-[512px] h-[512px]">
            <div className="absolute flex flex-row bottom-0 right-0 mb-4 mr-3 items-center">
              <a href={imageUrl} download={"image.jpg"}>
                <Button
                  disableRipple
                  className=" bg-transparent mb-1"
                  isIconOnly
                >
                  <IoDownloadOutline size={38}></IoDownloadOutline>
                </Button>
              </a>
              <Button
                onClick={uploadToGallery}
                disableRipple
                className=" bg-transparent"
                isIconOnly
              >
                <GrGallery size={30} />
              </Button>
            </div>
            <div>
              <Image
                height={512}
                width={512}
                alt="generatedImage"
                src={imageUrl}
              ></Image>
            </div>
          </div>
        ) : (
          <div className="flex justify-center items-center bg-zinc-200 dark:bg-zinc-800 w-[512px] h-[512px]">
            <CiImageOn size={150} />
          </div>
        )}
      </div>

      <div className="mt-5 flex items-center">
        <Textarea
          variant="bordered"
          placeholder="Enter model input"
          value={value}
          onValueChange={setValue}
          className=" w-80"
        />
        <div className="ml-5 flex flex-col">
          <Button
            isLoading={loading}
            onClick={() => generateImage()}
            color="danger"
          >
            Compute
          </Button>
        </div>
      </div>
    </div>
  );
}
